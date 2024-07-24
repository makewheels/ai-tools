package com.github.makewheels.aitools.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.food.Food;

import java.util.Date;

public class OssPathUtil {
    public static String getFoodTask(String userId, String taskId) {
        String createDate = DateUtil.format(new Date(), DatePattern.SIMPLE_MONTH_PATTERN);
        return "food/" + userId + "/" + createDate + "/" + taskId;
    }

    public static String getFoodImage(Food food, File file) {
        return getFoodTask(food.getUserId(), food.getId()) + "/original-image/"
                + file.getId() + "." + file.getExtension();
    }
}
