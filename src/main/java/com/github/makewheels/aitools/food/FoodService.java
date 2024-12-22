package com.github.makewheels.aitools.food;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.file.constants.FileType;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.utils.OssPathUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class FoodService {
    @Resource
    private FoodRepository foodRepository;
    @Resource
    private IdService idService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private FileService fileService;
    @Resource
    private GptService gptService;

    private static final String PROMPT = "根据图片中这顿饭吃的东西，" +
            "首分析每个食物的热量，蛋白质，碳水，脂肪等等健身所需的参数。" +
            "然后针对这顿饭，给出对于想健身增肌的人，给出饮食建议。回答尽量精简，不要太长。";

    public Food getById(String taskId) {
        return foodRepository.findById(taskId);
    }

    /**
     * 分析食物图片
     */
    public String analyzeImage(String userContent, String imageUrl) {
        String json = """
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": [
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
                        }
                    ]
                }
                """;
        String body = String.format(json, GptService.MODEL, userContent, imageUrl);

        return gptService.completion(body);
    }

    /**
     * 创建任务
     */
    public Food createTask(String extension) {
        // 创建文件
        CreateFileDTO createFileDTO = new CreateFileDTO();
        createFileDTO.setUploaderId(UserHolder.getUserId());
        createFileDTO.setExtension(extension);
        createFileDTO.setFileType(FileType.FOOD_IMAGE);
        File file = fileService.createNewFile(createFileDTO);
        log.info("创建文件：" + file.getId() + " " + JSON.toJSONString(file));

        // 创建识别食物任务
        Food food = new Food();
        food.setId(idService.getFoodId());
        food.setUserId(UserHolder.getUserId());
        food.setOriginalImageFileId(file.getId());
        mongoTemplate.save(food);
        log.info("创建食物识别任务：" + food.getId() + " " + JSON.toJSONString(food));

        // 反向更新文件的key
        file.setKey(OssPathUtil.getFoodImage(food, file));
        fileService.updateFile(file);
        return food;
    }

    /**
     * 启动任务
     */
    public void startTask(String taskId) {
        Food food = foodRepository.findById(taskId);
        food.setAnalyseStartTime(new Date());
        food.setStatus(FoodStatus.ANALYSING);
        food.setPrompt(PROMPT);
        mongoTemplate.save(food);
        log.info("启动食物识别任务：" + taskId + " " + JSON.toJSONString(food));

        File file = fileService.getById(food.getOriginalImageFileId());
        String imageUrl = fileService.getPresignedUrlByKey(file.getKey());
        String analyzeResult = analyzeImage(PROMPT, imageUrl);

        food.setResult(analyzeResult);
        food.setFinishTime(new Date());
        food.setStatus(FoodStatus.FINISHED);
        mongoTemplate.save(food);
        log.info("食物识别任务完成：" + taskId + " " + JSON.toJSONString(food));
    }

}
