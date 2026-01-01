package com.frank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity // 開啟響應式 的 基於方法層級的權限控制
public class AppSecurityConfiguration {

	@Autowired
	MyReactiveUserDetailsService myReactiveUserDetailsService;

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// 1、定義哪些請求需要認證，哪些不需要
		http.authorizeExchange(authorize -> {
			// 1.1、允許所有人都存取靜態資源；
			authorize.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

			// 1.2、剩下的所有請求都需要認證（登入）
			authorize.anyExchange().authenticated();
		});

		// 2、開啟預設的表單登入
		http.formLogin(formLoginSpec -> {
// formLoginSpec.loginPage("/haha");
		});

		// 3、安全控制:
		http.csrf(csrfSpec -> {
			csrfSpec.disable();
		});

		// 目前認證： 使用者名稱 是 user 密碼是預設產生。
		// 期望認證： 去資料庫查使用者名稱和密碼

		// 4、設定 認證規則： 如何去資料庫查詢到使用者;
		// Spring Security 底層使用 ReactiveAuthenticationManager 去查詢使用者資訊
		// ReactiveAuthenticationManager 有一個實作是
		// UserDetailsRepositoryReactiveAuthenticationManager： 使用者資訊去資料庫中查
		// UDRespAM 需要 ReactiveUserDetailsS​​ervice：
		// 我們只需要自己寫一個 ReactiveUserDetailsS​​ervice： 響應式的使用者詳情查詢服務
		http.authenticationManager(
				new UserDetailsRepositoryReactiveAuthenticationManager(myReactiveUserDetailsService));

		// http.addFilterAt()

		// 建構出安全性配置
		return http.build();
	}

}
