package com.frank.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import com.frank.config.converter.BookConverter;

/**
 *
 * 1、寫 AuthorRepositories 介面
 */
@EnableR2dbcRepositories // 開啟 R2dbc 倉庫功能；jpa
@Configuration
public class R2DbcConfiguration {

	@Bean // 取代容器中原來的
	@ConditionalOnMissingBean
	public R2dbcCustomConversions conversions() {

		//把我們的轉換器加入進去；效果新增了我們的 Converter
		return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new BookConverter());
	}
}