package com.yujian.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册 Sa-Token 登录鉴权拦截器
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SaTokenAuthInterceptor saTokenAuthInterceptor;

    /**
     * 注册鉴权拦截器，白名单接口不校验登录
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(saTokenAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/error",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/api-docs/**",
                        "/favicon.ico",
                        "/actuator/**"
                );
    }
}
