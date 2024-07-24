package com.github.makewheels.aitools.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.file.constants.FileStatus;
import com.github.makewheels.aitools.utils.IdService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {
    @Resource
    private OssService ossService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private IdService idService;
    @Resource
    private FileRepository fileRepository;

    public File createNewFile(CreateFileDTO createFileDTO) {
        File file = new File();
        file.setId(idService.getFileId());
        file.setUploaderId(createFileDTO.getUploaderId());
        file.setKey(createFileDTO.getKey());
        file.setFilename(FilenameUtils.getName(createFileDTO.getKey()));
        file.setExtension(createFileDTO.getExtension());
        file.setFileType(createFileDTO.getFileType());
        mongoTemplate.save(file);
        return file;
    }

    public void updateFile(File file) {
        mongoTemplate.save(file);
    }

    /**
     * 获取上传凭证
     */
    public JSONObject getUploadCredentials(String fileId) {
        File file = fileRepository.getById(fileId);
        JSONObject credentials = ossService.generateUploadCredentials(file.getKey());
        log.info("生成上传凭证, fileId = " + fileId + ", " + JSON.toJSONString(credentials));
        return credentials;
    }

    /**
     * 通知文件上传完成，和对象存储服务器确认，改变数据库File状态
     */
    public void uploadFinish(String fileId) {
        File file = fileRepository.getById(fileId);
        String key = file.getKey();
        log.info("FileService 处理文件上传完成, fileId = " + fileId + ", key = " + key);
        OSSObject object = ossService.getObject(key);
        ObjectMetadata objectMetadata = object.getObjectMetadata();
        file.setSize(objectMetadata.getContentLength());
        file.setUploadTime(objectMetadata.getLastModified());
        file.setFileStatus(FileStatus.READY);
        mongoTemplate.save(file);
    }

}
