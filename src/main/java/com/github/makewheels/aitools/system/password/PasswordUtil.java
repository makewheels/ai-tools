package com.github.makewheels.aitools.system.password;

import cn.hutool.core.io.FileUtil;
import com.github.makewheels.aitools.AiToolsApplication;
import com.github.makewheels.aitools.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 秘钥工具类
 */
@Slf4j
public class PasswordUtil {

    /**
     * 获取解密私钥
     */
    private static String getPrivateKey(String env) {
        String path = null;
        if (env.equals("dev")) {
            path = "D:/workSpace/~keys/" + ProjectUtils.PROJECT_NAME + "/privateKey.txt";
        } else if (env.equals("prod")) {
            path = "/root/keys/" + ProjectUtils.PROJECT_NAME + "/privateKey.txt";
        }
        if (FileUtil.exist(path)) {
            return FileUtil.readUtf8String(path);
        } else {
            return System.getenv("ai_tools_privateKey");
        }
    }

    /**
     * 获取密码文件
     */
    private static InputStream getPasswordFileInputStream(String env) {
        String path = null;
        if (env.equals("dev")) {
            path = "passwords-dev.properties";
        } else if (env.equals("prod")) {
            path = "passwords-prod.properties";
        }
        return AiToolsApplication.class.getResourceAsStream("/" + path);
    }

    /**
     * 获得解密之后的map
     */
    public static Map<String, String> getPlainTextMap(String env) {
        //加载解密私钥
        String privateKey = getPrivateKey(env);
        //加载密码文件
        Properties properties = new Properties();
        try {
            properties.load(getPasswordFileInputStream(env));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        //遍历密码文件
        Map<String, String> map = new HashMap<>(properties.size());
        Set<String> keySet = properties.stringPropertyNames();
        for (String key : keySet) {
            String cipher = properties.getProperty(key);
            //解密
            String plain = RSAUtil.decrypt(privateKey, cipher);
            //放入map
            map.put(key, plain);
        }
        return map;
    }

}