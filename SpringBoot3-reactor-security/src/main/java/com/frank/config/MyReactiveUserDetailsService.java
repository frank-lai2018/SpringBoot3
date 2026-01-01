package com.frank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class MyReactiveUserDetailsService implements ReactiveUserDetailsService {

	@Autowired
	DatabaseClient databaseClient;

	// 自訂如何依照使用者名稱去資料庫查詢使用者信息

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public Mono<UserDetails> findByUsername(String username) {

// PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		// 從資料庫查詢使用者、角色、權限所有資料的邏輯
		Mono<UserDetails> userDetailsMono = databaseClient
				.sql("select u.*,r.id rid,r.name,r.value,pm.id pid,pm.value pvalue,pm.description " +
                        "from t_user u " +
                        "left join t_user_role ur on ur.user_id=u.id " +
                        "left join t_roles r on r.id = ur.role_id " +
                        "left join t_role_perm rp on rp.role_id=r.id " +
                        "left join t_perm pm on rp.perm_id=pm.id " +
                        "where u.username = ? limit 1")
				.bind(0, username).fetch().one()// all()
				.map(map -> {
					UserDetails details = 
							User
							.builder()
							.username(username)
							.password(map.get("password").toString())
										// 自動呼叫密碼加密器把前端傳來的明文 encode
							//.passwordEncoder(str-> passwordEncoder.encode(str)) //為啥？ ？ ？ 
											//權限
							.authorities(new SimpleGrantedAuthority("ROLE_delete")) //預設不成功
							.roles("admin", "sale", "haha", "delete") // ROLE成功
							.build();

					// 角色和權限都被封裝成 SimpleGrantedAuthority
					// 角色有 ROLE_ 前綴， 權限沒有
					// hasRole：hasAuthority
					return details;
				});

		return userDetailsMono;
	}

}
