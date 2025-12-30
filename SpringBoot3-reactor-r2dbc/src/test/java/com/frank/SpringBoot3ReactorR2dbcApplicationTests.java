package com.frank;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.frank.dto.TAuthor;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import reactor.core.publisher.Mono;

//@SpringBootTest
class SpringBoot3ReactorR2dbcApplicationTests {

	// 思想：
	// 1.有了r2dbc，我們的應用在資料庫層面天然支援高並發、高吞吐量。
	// 2、並不能提升開發效率

	@Test
	void connection() throws IOException {

		// r2dbc基於全非同步、響應式、訊息驅動
		// jdbc:mysql://localhost:3306/test
		// r2dbc:mysql://localhost:3306/test

		// 0、MySQL配置
		MySqlConnectionConfiguration configuration = 
				MySqlConnectionConfiguration.builder()
				.host("192.168.32.128")
				.port(3306)
				
				.username("root")
				.password("aaaa1234")
				.database("test").build();

		// 1、取得連接工廠
		MySqlConnectionFactory connectionFactory = MySqlConnectionFactory.from(configuration);

		// 2、取得到連接，發送sql

		// JDBC： Statement： 封裝sql的
		// 3、資料發布者
		Mono.from(connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("select * from t_author where id=?id and name=?name")
						.bind("id", 1L) // 具名參數
						.bind("name", "張三").execute())
				.flatMap(result -> {
					return result.map(readable -> {
						Long id = readable.get("id", Long.class);
						String name = readable.get("name", String.class);
						return new TAuthor(id, name);
					});
				}).subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

		// 背壓； 不用回傳所有東西，基於請求量回傳；

		System.in.read();

	}

}
