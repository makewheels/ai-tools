package com.github.makewheels.ai_tools.system.interceptor;

import com.github.makewheels.ai_tools.system.interceptor.token.CheckTokenInterceptor;
import com.github.makewheels.ai_tools.system.interceptor.token.PutTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public PutTokenInterceptor getPutTokenInterceptor() {
        return new PutTokenInterceptor();
    }

    @Bean
    public CheckTokenInterceptor getCheckTokenInterceptor() {
        return new CheckTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 放token
        registry.addInterceptor(getPutTokenInterceptor())
                .addPathPatterns("/**");

        // 校验登录状态
        registry.addInterceptor(getCheckTokenInterceptor())
                .addPathPatterns("/save-token.html")
        ;

    }
}
