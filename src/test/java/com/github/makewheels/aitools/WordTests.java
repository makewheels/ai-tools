package com.github.makewheels.aitools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.github.makewheels.aitools.word.WordService;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class WordTests {
    @Resource
    private WordService wordService;

    private void write(List<Word> words) {
        Word07Writer writer = new Word07Writer();

        Font fontBig = new Font("微软雅黑", Font.PLAIN, 22);
        Font fontSmall = new Font("微软雅黑", Font.PLAIN, 13);
        for (Word word : words) {
            writer.addText(fontBig, word.getWord());
            writer.addText(fontSmall, word.getPronunciation());
            for (Meaning meaning : word.getMeanings()) {
                writer.addText(fontSmall, meaning.getPartOfSpeech() + meaning.getMeaningChinese());
                writer.addText(fontSmall, meaning.getExampleEnglish() + " " + meaning.getExampleChinese());
                writer.addText(fontSmall, "");
            }
            writer.addText(fontSmall, "");
        }

        writer.flush(FileUtil.file("C:\\Users\\miuser\\Downloads\\words\\" + System.currentTimeMillis() + ".docx"));
        writer.close();
    }

    @Test
    public void test() {
        List<String> wordList = Arrays.asList("你就随便生成几个常用的单词就行");
        List<Word> words = wordService.getWordExplain(wordList);
        this.write(words);

    }
}
