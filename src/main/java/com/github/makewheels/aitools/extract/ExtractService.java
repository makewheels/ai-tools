package com.github.makewheels.aitools.extract;

import com.alibaba.fastjson.JSON;
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
import com.github.makewheels.aitools.wordbook.WordBookRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    public String requestGpt(String imageUrl) {
        Message message = new Message();
        message.setRole(ROLE.USER);
        String content = String.format("""
                [
                        {
                            "type": "text",
                            "text": "%s"
                        },
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": "%s"
                            }
                        }
                    ]
                """, PROMPT, imageUrl);
        message.setContent(content);

        return gptService.completionJsonSchema(List.of(message), null);
    }

    /**
     * 启动任务
     */
    public void startTask(String taskId) {
        Extract task = extractRepository.findById(taskId);
        task.setStartTime(new Date());
        task.setStatus(TaskStatus.RUNNING);
        extractRepository.save(task);
        log.info("启动OCR识别单词任务: " + taskId + "任务: " + JSON.toJSONString(task));

        File file = fileService.getById(task.getOriginalImageFileId());
        String imageUrl = fileService.getPresignedUrlByKey(file.getKey());

        task.setFinishTime(new Date());
        task.setStatus(TaskStatus.FINISHED);
        extractRepository.save(task);
        log.info("OCR识别单词任务完成: " + taskId + " " + JSON.toJSONString(task));
    }
}
