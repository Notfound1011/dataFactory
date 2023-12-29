package com.phemex.dataFactory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description: 配置跨域请求处理
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有路径进行跨域处理
                .allowedOriginPatterns("*")  // 允许所有来源的请求
                .allowCredentials(true)  // 允许发送凭证信息，例如 Cookies
                .allowedMethods("GET", "POST", "DELETE", "PUT")  // 允许的请求方法
                .maxAge(3600);  // 预检请求的缓存时间，单位为秒
    }
}