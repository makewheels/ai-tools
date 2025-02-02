package com.github.makewheels.aitools;

import cn.hutool.core.io.FileUtil;
import com.github.makewheels.aitools.system.password.RSAUtil;
import com.github.makewheels.aitools.utils.ProjectUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class RSATests {
    @Test
    public void generateKeyPairs() {
        Map<String, String> map = RSAUtil.generateKeyPairs();
        String publicKey = map.get("publicKey");
        String privateKey = map.get("privateKey");
        System.out.println("publicKey = " + publicKey);
        System.out.println("privateKey = " + privateKey);
    }

    @Test
    @Disabled
    public void encrypt() {
        String plain = "";
        System.out.println("plainText = " + plain);

        String publicKey = FileUtil.readUtf8String(
                "D:\\workSpace\\~keys\\" + ProjectUtils.PROJECT_NAME + "\\publicKey.txt");

        String cipher = RSAUtil.encrypt(publicKey, plain);
        System.out.println("cipher = " + cipher);

    }

    @Test
    @Disabled
    public void decrypt() {
        String cipher = "";
        System.out.println("cipher = " + cipher);

        String privateKey = FileUtil.readUtf8String(
                "D:\\workSpace\\~keys\\" + ProjectUtils.PROJECT_NAME + "\\privateKey.txt");

        String plain = RSAUtil.decrypt(privateKey, cipher);
        System.out.println("plain = " + plain);

    }
}
