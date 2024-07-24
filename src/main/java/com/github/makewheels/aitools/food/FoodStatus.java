package com.github.makewheels.aitools.food;

public interface FoodStatus {
    // 数据库已创建
    String CREATED = "CREATED";

    // 图片上传已完成
    String IMAGE_UPLOADED = "IMAGE_UPLOADED";

    // 调用GPT分析中
    String ANALYSING = "ANALYSING";

    // 已完成
    String FINISHED = "FINISHED";
}
