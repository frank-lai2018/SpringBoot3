package com.frank.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //這是一個設定類別,給容器中放一個 WebMvcConfigurer 元件，就能自訂底層
public class WebMvcConfigCustomByBean {

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			//因為JDK8以後insterface可以有預設方法，所以可以只覆寫需要的方法
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/").setViewName("index");
				registry.addViewController("/login").setViewName("login");
				registry.addViewController("/register").setViewName("register");
				registry.addViewController("/main").setViewName("main");
			}
			
			@Override
			public void addResourceHandlers(
					org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/static/**") // 對外的虛擬路徑
						.addResourceLocations("classpath:/static/"); // 實際的檔案路徑
			}

			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				converters.add(new com.frank.converter.MyYamlHttpMessageConverter());
				WebMvcConfigurer.super.configureMessageConverters(converters);
			}
			
			
		};
	}
}
