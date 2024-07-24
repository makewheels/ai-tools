package com.github.makewheels.aitools.file;

import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.bean.File;
import com.github.makewheels.aitools.utils.IdService;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Resource
    private OssService ossService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private IdService idService;

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
}
