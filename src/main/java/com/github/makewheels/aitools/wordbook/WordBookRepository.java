package com.github.makewheels.aitools.wordbook;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class WordBookRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void save(WordBook wordBook) {
        mongoTemplate.save(wordBook);
    }

    public List<WordBook> listByUserId(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, WordBook.class);
    }
}
