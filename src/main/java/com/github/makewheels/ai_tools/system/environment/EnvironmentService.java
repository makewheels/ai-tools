package com.github.makewheels.ai_tools.system.environment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 环境，配置
 */
@Service
@Getter
public class EnvironmentService {
    @Value("${spring.profiles.active}")
    private String environment;

    /**
     * 环境判断
     */
    public boolean isDevelopmentEnv() {
        return environment.equals(Environment.DEVELOPMENT);
    }

    public boolean isProductionEnv() {
        return environment.equals(Environment.PRODUCTION);
    }

}
