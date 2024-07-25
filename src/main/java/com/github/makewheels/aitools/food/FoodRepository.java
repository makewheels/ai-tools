package com.github.makewheels.aitools.food;

import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FoodRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public Food findById(String taskId) {
        return mongoTemplate.findById(taskId, Food.class);
    }
}
