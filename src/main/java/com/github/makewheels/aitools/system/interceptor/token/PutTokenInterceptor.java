package com.github.makewheels.aitools.system.interceptor.token;

import com.github.makewheels.aitools.system.interceptor.InterceptorOrder;
import com.github.makewheels.aitools.user.User;
import com.github.makewheels.aitools.user.UserHolder;
import com.github.makewheels.aitools.user.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 把token放到ThreadLocal
 */
@Slf4j
@Component
public class PutTokenInterceptor implements HandlerInterceptor, Ordered {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        //通过token获取User，放入userHolder
        User user = userService.getUserByRequest(request);
        if (user != null) {
            UserHolder.set(user);
        }
        return true;
    }

    @Override
    public int getOrder() {
        return InterceptorOrder.PUT_TOKEN;
    }
}
