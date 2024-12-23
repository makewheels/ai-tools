package com.github.makewheels.aitools;

import com.github.makewheels.aitools.word.WordHelper;
import com.github.makewheels.aitools.word.WordService;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class WordTests {
    @Resource
    private WordService wordService;
    @Resource
    private WordHelper wordHelper;

    private void requestWords() {
        List<Word> wordList = wordService.getWordExplain(
                "journey, curious, reason, beautiful, admire, quiet, solve, arrive, improve, strong");
        Map<String, String> imageMap = wordService.getImage(wordList);
        wordHelper.export(wordList, imageMap);
    }

    @Test
    public void getWordsExplain() {
        this.requestWords();
        List<Word> wordList = wordHelper.parse(WordHelper.WORDS_FOLDER);
        wordHelper.writeDocFile(wordList);
    }


}
