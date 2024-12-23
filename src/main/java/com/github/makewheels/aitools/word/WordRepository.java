package com.github.makewheels.aitools.word;

import cn.hutool.core.util.RandomUtil;
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

    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), Word.class);
    }

    public Word randomPick() {
        // 获取集合中的总记录数
        long count = mongoTemplate.count(new Query(), Word.class);
        if (count == 0) {
            return null; // 如果没有记录，返回 null
        }

        // 生成一个随机的 offset 值
        int offset = RandomUtil.randomInt((int) count);

        // 使用 skip 和 limit 获取一条记录
        Query query = new Query().skip(offset).limit(1);
        return mongoTemplate.findOne(query, Word.class);
    }
}
