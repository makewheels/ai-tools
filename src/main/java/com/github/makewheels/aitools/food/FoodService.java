package com.github.makewheels.aitools.food;

import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.file.constants.FileType;
import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.utils.OssPathUtil;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class FoodService {
    @Resource
    private FoodRepository foodRepository;
    @Resource
    private IdService idService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private FileService fileService;

    public Food createTask(String extension) {
        // 创建文件
        CreateFileDTO createFileDTO = new CreateFileDTO();
        createFileDTO.setUploaderId(UserHolder.getUserId());
        createFileDTO.setExtension(extension);
        createFileDTO.setFileType(FileType.FOOD_IMAGE);
        File file = fileService.createNewFile(createFileDTO);

        // 创建识别食物任务
        Food food = new Food();
        food.setId(idService.getFoodId());
        food.setUserId(UserHolder.getUserId());
        food.setOriginalImageFileId(file.getId());
        mongoTemplate.save(food);

        // 反向更新文件的key
        file.setKey(OssPathUtil.getFoodImage(food, file));
        fileService.updateFile(file);
        return food;
    }
}
