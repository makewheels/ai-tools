package com.github.makewheels.ai_tools;

import cn.hutool.core.io.FileUtil;
import com.github.makewheels.ai_tools.system.password.RSAUtil;
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

        String publicKey = FileUtil.readUtf8String("D:\\workSpace\\~keys\\ai-tools\\publicKey.txt");

        String cipher = RSAUtil.encrypt(publicKey, plain);
        System.out.println("cipher = " + cipher);

    }

    @Test
    @Disabled
    public void decrypt() {
        String cipher = "";
        System.out.println("cipher = " + cipher);

        String privateKey = FileUtil.readUtf8String("D:\\workSpace\\~keys\\ai-tools\\privateKey.txt");

        String plain = RSAUtil.decrypt(privateKey, cipher);
        System.out.println("plain = " + plain);

    }
}
