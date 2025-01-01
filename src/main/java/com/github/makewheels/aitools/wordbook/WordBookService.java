package com.github.makewheels.aitools.wordbook;

import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.word.WordRepository;
import com.github.makewheels.aitools.word.bean.Word;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    @Resource
    private IdService idService;

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

    /**
     * 把单词添加到单词本
     */
    public void addToWordBook(String userId, List<String> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        for (String content : contentList) {
            if (wordBookRepository.exist(userId, content)) {
                continue;
            }

            WordBook wordBook = new WordBook();
            wordBook.setId(idService.getWoodBookId());
            wordBook.setUserId(userId);
            wordBook.setContent(content);

            wordBookRepository.save(wordBook);
        }
    }
}
