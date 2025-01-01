package com.github.makewheels.aitools.wordbook;

import cn.hutool.core.util.RandomUtil;
import com.github.makewheels.aitools.utils.IdService;
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
    @Resource
    private IdService idService;

    public void save(WordBook wordBook) {
        mongoTemplate.save(wordBook);
    }

    public List<WordBook> listByUserId(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, WordBook.class);
    }

    public boolean exist(String userId, String content) {
        Query query = Query.query(
                Criteria.where("userId").is(userId)
                        .and("content").is(content)
        );
        return mongoTemplate.exists(query, WordBook.class);
    }

    public WordBook randomPick(String userId) {
        // 获取总数
        Query query = Query.query(Criteria.where("userId").is(userId));
        long count = mongoTemplate.count(query, WordBook.class);
        if (count == 0) {
            return null;
        }

        // 生成一个随机的 offset 值
        long randomOffset = RandomUtil.randomLong(count);

        // 使用 skip 和 limit 获取一条记录
        query.skip(randomOffset).limit(1);
        return mongoTemplate.findOne(query, WordBook.class);
    }

}
