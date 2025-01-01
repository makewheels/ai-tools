package com.github.makewheels.aitools.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
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

import java.time.Duration;

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
        log.info("创建文件：" + file.getId() + " " + JSON.toJSONString(file));
        mongoTemplate.save(file);
        return file;
    }

    /**
     * 上传文件
     */
    public void uploadFile(String fileId, java.io.File uploadFile) {
        File databaseFile = fileRepository.getById(fileId);
        ossService.uploadFile(databaseFile.getKey(), uploadFile);
    }

    public void deleteFile(String fileId) {
        File file = fileRepository.getById(fileId);
        if (file == null) {
            return;
        }
        ossService.deleteByKey(file.getKey());
        fileRepository.deleteById(fileId);
    }

    public void updateFile(File file) {
        mongoTemplate.save(file);
    }

    public File getById(String id) {
        return fileRepository.getById(id);
    }

    public String getPresignedUrlByKey(String key) {
        return ossService.generatePresignedUrl(key, Duration.ofMinutes(10));
    }

    public String getPresignedUrlByFileId(String fileId) {
        if (fileId == null){
            return null;
        }
        File file = getById(fileId);
        return getPresignedUrlByKey(file.getKey());
    }

    /**
     * 获取上传凭证
     */
    public JSONObject getUploadCredentials(String fileId) {
        File file = fileRepository.getById(fileId);
        JSONObject credentials = ossService.generateUploadCredentials(file.getKey());
        log.info("获取上传凭证, fileId = " + fileId + ", " + JSON.toJSONString(credentials));
        return credentials;
    }

    /**
     * 把从OSS请求回来的文件信息，装填到file对象上
     */
    public void setObjectInfo(File file, OSSObject object) {
        file.setKey(file.getKey());
        ObjectMetadata metadata = object.getObjectMetadata();
        file.setSize(metadata.getContentLength());
        file.setFilename(FilenameUtils.getName(file.getKey()));
        file.setExtension(FilenameUtils.getExtension(file.getKey()));
        file.setStorageClass(metadata.getObjectStorageClass().toString());
        file.setUploadTime(metadata.getLastModified());
    }

    /**
     * 把从OSS请求回来的文件信息，装填到file对象上
     */
    public void setObjectInfo(File file, OSSObjectSummary objectSummary) {
        file.setKey(objectSummary.getKey());
        file.setSize(objectSummary.getSize());
        file.setFilename(FilenameUtils.getName(file.getKey()));
        file.setExtension(FilenameUtils.getExtension(file.getKey()));
        file.setStorageClass(objectSummary.getStorageClass());
        file.setUploadTime(objectSummary.getLastModified());
    }

    /**
     * 通知文件上传完成，和对象存储服务器确认，改变数据库File状态
     */
    public void uploadFinish(String fileId) {
        File file = fileRepository.getById(fileId);
        String key = file.getKey();
        log.info("FileService 处理文件上传完成, fileId = " + fileId + ", key = " + key);
        OSSObject ossObject = ossService.getObject(key);
        setObjectInfo(file, ossObject);
        file.setFileStatus(FileStatus.READY);
        mongoTemplate.save(file);
    }

}
