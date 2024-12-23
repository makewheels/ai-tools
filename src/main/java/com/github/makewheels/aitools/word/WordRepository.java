package com.github.makewheels.aitools.word;

import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


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

    public void delete(String id){
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), Word.class);
    }
}
