package com.github.makewheels.aitools.file;

import com.alibaba.fastjson.JSONObject;
import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private FileService fileService;

    /**
     * 获取上传凭证
     */
    @GetMapping("getUploadCredentials")
    public Result<JSONObject> getUploadCredentials(@RequestParam String fileId) {
        return Result.ok(fileService.getUploadCredentials(fileId));
    }

    /**
     * 当前文件上传完成时
     */
    @GetMapping("uploadFinish")
    public Result<Void> uploadFinish(@RequestParam String fileId) {
        fileService.uploadFinish(fileId);
        return Result.ok();
    }

}
