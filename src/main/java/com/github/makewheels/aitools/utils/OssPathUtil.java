package com.github.makewheels.aitools.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.food.Food;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class OssPathUtil {
    private static String getDate() {
        return DateUtil.format(new Date(), DatePattern.SIMPLE_MONTH_PATTERN);
    }

    public static String getFoodTaskFolder(String userId, String taskId) {
        return "food/" + userId + "/" + getDate() + "/" + taskId;
    }

    public static String getFoodImageFile(Food food, File file) {
        return getFoodTaskFolder(food.getUserId(), food.getId()) + "/original-image/"
                + file.getId() + "." + file.getExtension();
    }

    public static String getWordFolder(String word) {
        return "words/" + StringUtils.left(word, 1) + "/" + word;
    }

    public static String getWordImagesFolder(String word) {
        return getWordFolder(word) + "/" + "images";
    }

    public static String getWordImageFile(String word, String imagePromptMd5) {
        return getWordImagesFolder(word) + "/" + imagePromptMd5 + ".png";
    }
}
