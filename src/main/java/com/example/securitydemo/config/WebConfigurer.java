package com.example.securitydemo.config;

import java.util.Arrays;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Tomcat相关定义
 *
 *
 */
@Slf4j
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {
    @Override
    public void customize(WebServerFactory factory) {
        log.info("Web application customize");
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application fully configured");
    }

    /**
     * 注册一个 跨域配置 的bean 供SecurityConfig使用
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","HEAD", "OPTION"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
