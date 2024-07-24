package com.github.makewheels.ai_tools.system.health;

import cn.hutool.core.date.DateUtil;
import com.github.makewheels.ai_tools.system.context.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class HealthCheckController {
    @GetMapping("healthCheck")
    public String healthCheck() {
        HttpServletRequest request = RequestUtil.getRequest();
        log.info("healthCheck---" + DateUtil.formatDateTime(new Date()) + "-" + request.getRequestURL());
        return "ok " + System.currentTimeMillis();
    }
}
