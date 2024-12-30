package com.github.makewheels.aitools.word;

import cn.hutool.core.util.RandomUtil;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class WordRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void save(Word word) {
        mongoTemplate.save(word);
    }

    public Word getByContent(String content) {
        Query query = Query.query(Criteria.where("content").is(content));
        return mongoTemplate.findOne(query, Word.class);
    }

    public Map<String, Word> listByContentList(List<String> contentList) {
        Query query = Query.query(Criteria.where("content").in(contentList));
        List<Word> wordList = mongoTemplate.find(query, Word.class);
        return wordList.stream().collect(Collectors.toMap(Word::getContent, Function.identity()));
    }

    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), Word.class);
    }

    public Word randomPick() {
        // 获取集合中的总记录数
        long count = mongoTemplate.count(new Query(), Word.class);
        if (count == 0) {
            return null;
        }

        // 生成一个随机的 offset 值
        long offset = RandomUtil.randomLong(count);

        // 使用 skip 和 limit 获取一条记录
        Query query = new Query().skip(offset).limit(1);
        return mongoTemplate.findOne(query, Word.class);
    }
}
