package com.github.makewheels.aitools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.fastjson.JSON;
import com.github.makewheels.aitools.word.WordService;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class WordTests {
    @Resource
    private WordService wordService;

    private static final String FOLDER = "C:/Users/miuser/Downloads/words";

    private File getImageFile(String word, String imagePromptMd5) {
        return new File(FOLDER, word + "/" + imagePromptMd5 + ".png");
    }

    private void downloadImages(List<Word> words, Map<String, String> imageMap) {
        for (Word word : words) {
            for (Meaning meaning : word.getMeanings()) {
                File imageFile = getImageFile(word.getWord(), meaning.getImagePromptMd5());
                String imagePrompt = meaning.getImagePrompt();
                String imageUrl = imageMap.get(DigestUtil.md5Hex(imagePrompt));

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
                writer.addText(fontSmall, meaning.getExampleEnglish());
                writer.addText(fontSmall, meaning.getExampleChinese());
                File imageFile = this.getImageFile(word.getWord(), meaning.getImagePromptMd5());
                if (imageFile.exists()) {
                    writer.addPicture(imageFile, 192, 192);
                }
                writer.addText(fontSmall, "");
            }
            writer.addText(fontSmall, "");

            File wordJsonFile = new File(FOLDER, word.getWord() + "/" + word.getWord() + ".json");
            FileUtil.writeUtf8String(JSON.toJSONString(word, true), wordJsonFile);
        }

        writer.flush(new File(FOLDER, System.currentTimeMillis() + ".docx"));
        writer.close();
    }

    @Test
    public void getWordsExplain() {
        List<Word> wordList = wordService.getWordExplain(List.of("Pick up  ,Bummer  ,Grab  "));
//        Map<String, String> imageMap = wordService.getImage(wordList);
//        this.downloadImages(wordList, imageMap);
        this.write(wordList);
    }
}
