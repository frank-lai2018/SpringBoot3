package com.frank.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import com.frank.converter.MyYamlHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration //這是一個設定類別,給容器中放一個 WebMvcConfigurer 元件，就能自訂底層
public class WebMvcConfigCustomByImplements implements WebMvcConfigurer {

	// 可以覆寫 WebMvcConfigurer 介面中的方法來自訂 Spring MVC 的行為
	// 例如，添加攔截器、配置視圖解析器、設定靜態資源路徑等
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 保留先前規則
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		//自己寫規則
      registry.addResourceHandler("/static/**")//當請求為 /static/ 開頭
	      .addResourceLocations("classpath:/a/","classpath:/b/")//對應到 classpath 下的 a 和 b 目錄
	      .setCacheControl(CacheControl.maxAge(1180, TimeUnit.SECONDS));
	}

	@Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 置前，優先匹配
        converters.add(0, new MyYamlHttpMessageConverter());
    }

	

	
}
