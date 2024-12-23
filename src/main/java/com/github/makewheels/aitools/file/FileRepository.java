package com.github.makewheels.aitools.file;

import com.github.makewheels.aitools.file.bean.File;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 根据id查文件
     */
    public File getById(String id) {
        return mongoTemplate.findById(id, File.class);
    }

    /**
     * 根据id批量查文件
     */
    public List<File> getByIds(List<String> ids) {
        return mongoTemplate.find(Query.query(Criteria.where("id").in(ids)), File.class);
    }

    /**
     * 删除文件
     */
    public void deleteById(String fileId) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(fileId)), File.class);
    }
}
