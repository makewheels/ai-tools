package com.github.makewheels.aitools.extract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.file.constants.FileType;
import com.github.makewheels.aitools.food.TaskStatus;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.gpt.Message;
import com.github.makewheels.aitools.gpt.ROLE;
import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.utils.OssPathUtil;
import com.github.makewheels.aitools.wordbook.WordBook;
import com.github.makewheels.aitools.wordbook.WordBookRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ExtractService {
    @Resource
    private ExtractRepository extractRepository;
    @Resource
    private FileService fileService;
    @Resource
    private IdService idService;
    @Resource
    private WordBookRepository wordBookRepository;
    @Resource
    private GptService gptService;

    private static final String PROMPT = """
            请从用户手写的图片中提取出单词，并返回一个JSON格式的列表，注意不要提取印刷的文本
            示例返回值：
            {
                "words": [
                    "word1","word2"
                ]
            }
            """;

    private static final String JSON_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "words": {
                  "type": "array",
                  "items": {
                    "type": "string",
                    "description": "The word extracted from the image",
                    "additionalProperties": false
                  }
                }
              },
              "required": [
                "words"
              ],
              "additionalProperties": false
            }
            """;

    /**
     * 创建任务
     */
    public Extract createTask(String extension) {
        // 创建文件
        CreateFileDTO createFileDTO = new CreateFileDTO();
        createFileDTO.setUploaderId(UserHolder.getUserId());
        createFileDTO.setExtension(extension);
        createFileDTO.setFileType(FileType.OCR_WORD_IMAGE);
        File file = fileService.createNewFile(createFileDTO);

        // 创建任务
        Extract task = new Extract();
        task.setId(idService.getExtractId());
        task.setUserId(UserHolder.getUserId());
        task.setOriginalImageFileId(file.getId());
        extractRepository.save(task);
        log.info("创建OCR识别单词任务: " + task.getId() + " " + JSON.toJSONString(task));

        // 反向更新文件的key
        file.setKey(OssPathUtil.getExtractImageFile(task, file));
        fileService.updateFile(file);
        return task;
    }

    /**
     * 请求gpt
     */
    public List<String> extractWords(String imageUrl) {
        Message message = new Message();
        message.setRole(ROLE.USER);

        JSONArray contentList = new JSONArray();
        JSONObject contentText = new JSONObject();
        contentText.put("type", "text");
        contentText.put("text", PROMPT);

        JSONObject contentImage = new JSONObject();
        contentImage.put("type", "image_url");
        JSONObject imageUrlJSONObject = new JSONObject();
        imageUrlJSONObject.put("url", imageUrl);
        contentImage.put("image_url", imageUrlJSONObject);

        contentList.add(contentText);
        contentList.add(contentImage);

        message.setContent(contentList);

        String response = gptService.completionJsonSchema(List.of(message), JSON_SCHEMA);
        return JSON.parseObject(response).getJSONArray("words").toJavaList(String.class);
    }

    /**
     * 把单词添加到单词本
     */
    public void addToWordBook(String userId, List<String> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        for (String content : contentList) {
            if (wordBookRepository.exist(userId, content)) {
                continue;
            }

            WordBook wordBook = new WordBook();
            wordBook.setId(idService.getWoodBookId());
            wordBook.setUserId(userId);
            wordBook.setContent(content);

            wordBookRepository.save(wordBook);
        }
    }

    /**
     * 启动任务
     */
    public void startTask(String taskId) {
        Extract task = extractRepository.findById(taskId);
        task.setStartTime(new Date());
        task.setStatus(TaskStatus.RUNNING);
        extractRepository.save(task);
        log.info("启动OCR识别单词任务 taskId = " + taskId + ", 任务 = " + JSON.toJSONString(task));

        File file = fileService.getById(task.getOriginalImageFileId());
        String imageUrl = fileService.getPresignedUrlByKey(file.getKey());

        // 提取单词
        List<String> resultWordList = this.extractWords(imageUrl);
        log.info("OCR提取到单词结果: " + JSON.toJSONString(resultWordList));
        task.setResultWordList(resultWordList);
        task.setFinishTime(new Date());
        task.setStatus(TaskStatus.FINISHED);
        extractRepository.save(task);
        log.info("OCR识别单词任务完成: " + taskId + " " + JSON.toJSONString(task));

        // 保存到单词本
        this.addToWordBook(task.getUserId(), resultWordList);
    }

    public Extract getById(String taskId) {
        return extractRepository.findById(taskId);
    }
}
