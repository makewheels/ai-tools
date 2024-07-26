package com.github.makewheels.aitools.system.redis;

import cn.hutool.core.date.DateUtil;
import com.github.makewheels.aitools.utils.ProjectUtils;

import java.util.Date;

public class RedisKey {
    private static final String ROOT = ProjectUtils.PROJECT_NAME;

    private static final String USER = ROOT + ":user";

    private static final String INCREASE_SHORT_ID = ROOT + ":increaseShortId";
    private static final String INCREASE_LONG_ID = ROOT + ":increaseLongId";

    public static String token(String token) {
        return USER + ":token:" + token;
    }

    public static String increaseShortId() {
        return INCREASE_SHORT_ID + ":" + DateUtil.formatDate(new Date());
    }

    public static String increaseLongId(long timeUnit) {
        return INCREASE_LONG_ID + ":" + timeUnit;
    }
}
