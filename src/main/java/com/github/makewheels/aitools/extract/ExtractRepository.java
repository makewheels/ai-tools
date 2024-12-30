package com.github.makewheels.aitools.extract;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ExtractRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void save(Extract task) {
        mongoTemplate.save(task);
    }

    public Extract findById(String id) {
        return mongoTemplate.findById(id, Extract.class);
    }
}
