package com.github.makewheels.aitools.wordbook;

import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.word.WordRepository;
import com.github.makewheels.aitools.word.bean.Word;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WordBookService {
    @Resource
    private WordBookRepository wordBookRepository;
    @Resource
    private WordRepository wordRepository;

    public List<WordBookResponse> list() {
        List<WordBookResponse> result = new ArrayList<>();

        List<WordBook> wordBookList = wordBookRepository.listByUserId(UserHolder.getUserId());
        Map<String, Word> wordMap = wordRepository.listByContentList(Lists.transform(wordBookList, WordBook::getContent));

        for (WordBook wordBook : wordBookList) {
            WordBookResponse wordBookResponse = new WordBookResponse();
            wordBookResponse.setId(wordBook.getId());
            wordBookResponse.setContent(wordBook.getContent());
            Word word = wordMap.get(wordBook.getContent());
            wordBookResponse.setPronunciation(word.getPronunciation());
            wordBookResponse.setMeanings(word.getMeanings());
            result.add(wordBookResponse);
        }

        return result;
    }
}
