package com.github.makewheels.aitools.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OssService extends BaseOssService {
    @Value("${aliyun.oss.bucket}")
    private void setBucket(String bucket) {
        super.bucket = bucket;
    }

    @Value("${aliyun.oss.endpoint}")
    private void setEndpoint(String endpoint) {
        super.endpoint = endpoint;
    }

    @Value("${aliyun.oss.accessKeyId}")
    private void setAccessKeyId(String accessKeyId) {
        super.accessKeyId = accessKeyId;
    }

    @Value("${aliyun.oss.secretKey}")
    private void setSecretKey(String secretKey) {
        super.secretKey = secretKey;
    }
}
