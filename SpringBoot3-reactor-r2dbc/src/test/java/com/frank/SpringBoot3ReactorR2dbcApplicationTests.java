package com.frank;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;

import com.frank.dto.TAuthor;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import reactor.core.publisher.Mono;

@SpringBootTest
class SpringBoot3ReactorR2dbcApplicationTests {

	// 思想：
	// 1.有了r2dbc，我們的應用在資料庫層面天然支援高並發、高吞吐量。
	// 2、並不能提升開發效率

	//最佳實務： 提升生產效率的做法
	 //1、Spring Data R2DBC，基礎的CRUD用 R2dbcRepository 提供好了
	 //2、自訂複雜的SQL（單表）： @Query；
	 //3、多表查詢複雜結果集： DatabaseClient 自訂SQL及結果封裝；


	 //Spring Data 提供的兩個核心底層元件

	 @Autowired // join查詢不好做；單表查詢用
	 R2dbcEntityTemplate r2dbcEntityTemplate; //CRUD API; 更多API操作範例： https://docs.spring.io/spring-data/relational/reference/r2dbc/entity-persistence.html


	 @Autowired //貼近底層，join操作好做； 複雜查詢好用
	 DatabaseClient databaseClient; //資料庫客戶端
	
	@Test
	void databaseClient() throws IOException {

		// 底層操作
		databaseClient.sql("select * from t_author")
				// .bind(0,2L)
				.fetch() // 抓取數據
				.all()// 返回所有
				.map(map -> { // map == bean 屬性=值
					System.out.println("map = " + map);
					String id = map.get("id").toString();
					String name = map.get("name").toString();
					return new TAuthor(Long.parseLong(id), name);
				}).subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));
		System.in.read();

	}

	@Test
	void r2dbcEntityTemplate() throws IOException {

		// Query By Criteria: QBC

		// 1、Criteria建構查詢條件 where id=1 and name=張三
		Criteria criteria = Criteria.empty().and("id").is(1L).and("name").is("張三");

		// 2、封裝為 Query 對象
		Query query = Query.query(criteria);

		r2dbcEntityTemplate.select(query, TAuthor.class)
				.subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

		System.in.read();
	}

	@Test
	void connection() throws IOException {

		// r2dbc基於全非同步、響應式、訊息驅動
		// jdbc:mysql://localhost:3306/test
		// r2dbc:mysql://localhost:3306/test

		// 0、MySQL配置
		MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder().host("192.168.32.128")
				.port(3306)

				.username("root").password("aaaa1234").database("test").build();

		// 1、取得連接工廠
		MySqlConnectionFactory connectionFactory = MySqlConnectionFactory.from(configuration);

		// 2、取得到連接，發送sql

		// JDBC： Statement： 封裝sql的
		// 3、資料發布者
		Mono.from(connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("select * from t_author where id=?id and name=?name").bind("id", 1L) // 具名參數
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
