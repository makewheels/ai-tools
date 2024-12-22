package com.github.makewheels.aitools;

import com.alibaba.fastjson.JSONArray;
import com.github.makewheels.aitools.word.WordService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class WordTests {
    @Resource
    private WordService wordService;

    @Test
    public void test() {
        List<String> wordList = Arrays.asList("hello", "world");
        JSONArray wordExplain = wordService.getWordExplain(wordList);
        System.out.println(wordExplain.toJSONString());
    }
}
