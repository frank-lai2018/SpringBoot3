package com.frank.crud.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiUiConfig {

	/**
	 * 分組設定
	 * 
	 * @return
	 */
	@Bean
	public GroupedOpenApi empApi() {
		return GroupedOpenApi.builder().group("員工管理").pathsToMatch("/emp/**", "/emps").build();
	}

	@Bean
	public GroupedOpenApi deptApi() {
		return GroupedOpenApi.builder().group("部門管理").pathsToMatch("/dept/**", "/depts").build();
	}

	@Bean
	public OpenAPI docsOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("SpringBoot3-CRUD API").description("專門測試介面檔").version("v0.0.1")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.externalDocs(new ExternalDocumentation().description("哈哈 Wiki Documentation")
						.url("https://springshop.wiki.github.org/docs"));
	}
}
