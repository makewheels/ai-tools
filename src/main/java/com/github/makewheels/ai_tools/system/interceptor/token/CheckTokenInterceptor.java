package com.github.makewheels.ai_tools.system.interceptor.token;

import com.github.makewheels.ai_tools.system.environment.EnvironmentService;
import com.github.makewheels.ai_tools.system.interceptor.InterceptorOrder;
import com.github.makewheels.ai_tools.user.User;
import com.github.makewheels.ai_tools.user.UserHolder;
import com.github.makewheels.ai_tools.user.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 校验需要登录的接口
 */
@Slf4j
@Component
public class CheckTokenInterceptor implements HandlerInterceptor, Ordered {
    @Resource
    private UserService userService;
    @Resource
    private EnvironmentService environmentService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler) throws IOException, URISyntaxException {
        //通过token获取User
        User user = userService.getUserByRequest(request);

        //找到了用户，校验通过
        if (user != null) {
            response.setStatus(200);
            return true;
        }

        //如果不通过，让他登录
        response.setStatus(403);
        return false;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception e) {
        UserHolder.remove();
    }

    @Override
    public int getOrder() {
        return InterceptorOrder.CHECK_TOKEN;
    }
}
