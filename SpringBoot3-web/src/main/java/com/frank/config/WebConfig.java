package com.frank.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new com.frank.converter.MyYamlHttpMessageConverter());
        log.info("Registered YamlHttpMessageConverter. Current converters:");
        for (int i = 0; i < converters.size(); i++) {
            log.info("#{} -> {}", i, converters.get(i).getClass().getName());
        }
    }
}