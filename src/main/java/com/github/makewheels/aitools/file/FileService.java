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
