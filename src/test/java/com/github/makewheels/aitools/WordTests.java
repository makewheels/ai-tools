package com.github.makewheels.aitools;

import cn.hutool.http.HttpUtil;
import cn.hutool.poi.word.Word07Writer;
import com.github.makewheels.aitools.word.WordService;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class WordTests {
    @Resource
    private WordService wordService;

    private static final String FOLDER = "C:\\Users\\miuser\\Downloads\\words\\";

    private void downloadImages(List<Word> words) {
        for (Word word : words) {
            for (Meaning meaning : word.getMeanings()) {
                String imagePromptMd5 = meaning.getImagePromptMd5();
                String imageUrl = meaning.getImageUrl();
                File imageFile = new File(FOLDER, "images/" + imagePromptMd5 + ".png");
                meaning.setImageFilePath(imageFile.getAbsolutePath());
                log.info("下载文件 " + imageFile.getName() + " " + imageUrl);
                HttpUtil.downloadFile(imageUrl, imageFile);
            }
        }
    }

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
                writer.addPicture(new File(meaning.getImageFilePath()), 256, 256);
                writer.addText(fontSmall, "");
            }
            writer.addText(fontSmall, "");
        }

        writer.flush(new File(FOLDER, "doc/" + System.currentTimeMillis() + ".docx"));
        writer.close();
    }

    @Test
    public void test() {
        List<String> wordList = Arrays.asList("你就随便生成几个常用的单词就行");
        List<Word> words = wordService.getWordExplain(wordList);
        this.downloadImages(words);
        this.write(words);

    }
}
