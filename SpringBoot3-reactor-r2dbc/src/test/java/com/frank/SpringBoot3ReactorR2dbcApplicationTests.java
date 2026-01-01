package com.frank;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;

import com.frank.dto.TAuthor;
import com.frank.dto.TBook;
import com.frank.repositories.AuthorRepositories;
import com.frank.repositories.BookRepositories;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
class SpringBoot3ReactorR2dbcApplicationTests {

	// 思想：
	// 1.有了r2dbc，我們的應用在資料庫層面天然支援高並發、高吞吐量。
	// 2、並不能提升開發效率

	// 最佳實務： 提升生產效率的做法
	// 1、Spring Data R2DBC，基礎的CRUD用 R2dbcRepository 提供好了
	// 2、自訂複雜的SQL（單表）： @Query；
	// 3、多表查詢複雜結果集： DatabaseClient 自訂SQL及結果封裝；

	// Spring Data 提供的兩個核心底層元件

	@Autowired // join查詢不好做；單表查詢用
	R2dbcEntityTemplate r2dbcEntityTemplate; // CRUD API; 更多API操作範例：
												// https://docs.spring.io/spring-data/relational/reference/r2dbc/entity-persistence.html

	@Autowired // 貼近底層，join操作好做； 複雜查詢好用
	DatabaseClient databaseClient; // 資料庫客戶端

	@Autowired
	AuthorRepositories authorRepositories;

	@Autowired
	BookRepositories bookRepositories;
	
	@Test
	void oneToN() throws IOException {

		// databaseClient.sql("select a.id aid,a.name,b.* from t_author a " +
		// "left join t_book b on a.id = b.author_id " +
		// "order by a.id")
		// .fetch()
		// .all(row -> {
		//
		// })

		// 1~6
		// 1：false 2：false 3:false 4: true 8:true 5:false 6:false 7:false 8:true 9:false
		// 10:false
		// [1,2,3]
		// [4,8]
		// [5,6,7]
		// [8]
		// [9,10]
		// bufferUntilChanged：
		// 如果下一個判定值比起上一個發生了變化就開一個新buffer保存，如果沒有變化就保存到原buffer中

		// Flux.just(1,2,3,4,8,5,6,7,8,9,10)
		// .bufferUntilChanged(integer -> integer%4==0 )
		// .subscribe(list-> System.out.println("list = " + list));
		; // 自備分組

		Flux<TAuthor> flux = databaseClient
				.sql("select a.id aid,a.name,b.* from t_author a " + "left join t_book b on a.id = b.author_id "
						+ "order by a.id")
				.fetch().all().bufferUntilChanged(rowMap -> Long.parseLong(rowMap.get("aid").toString())).map(list -> {
					TAuthor tAuthor = new TAuthor();
					Map<String, Object> map = list.get(0);
					tAuthor.setId(Long.parseLong(map.get("aid").toString()));
					tAuthor.setName(map.get("name").toString());
					// 查到的所有圖書
					List<TBook> tBooks = list.stream()
							.filter(ele -> ele.get("id") != null).map(ele -> {
						TBook tBook = new TBook();

						tBook.setId(Long.parseLong(ele.get("id").toString()));
						tBook.setAuthorId(Long.parseLong(ele.get("author_id").toString()));
						tBook.setTitle(ele.get("title").toString());
						return tBook;
					}).collect(Collectors.toList());

					tAuthor.setBooks(tBooks);
					return tAuthor;
				});// Long 數位快取 -127 - 127；// 物件比較需要自己寫好equals方法

		flux.subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

		System.in.read();

	}
	
    @Test
    void author() throws IOException {
        authorRepositories.findById(1L)
                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        System.in.read();
    }

	@Test
	void bookRepositories() throws IOException {

		bookRepositories.findAll().subscribe(tBook -> System.out.println("tBook = " + tBook));

		// 1-1： 第一種方式: 自訂轉換器封裝
		bookRepositories.haha​​Book(1L).subscribe(tBook -> System.out.println("haha​​Book tBook = " + tBook));

		// 自訂轉換器 Converter<Row, TBook> ： 把資料庫的row轉成 TBook；所有TBook的結果封裝都用這個
		// 工作時機： Spring Data 發現方法簽章只要是回傳 TBook。 利用自訂轉換器進行工作

		// 對先前的CRUD產生影響; 錯誤：Column name 'name' does not exist
		// 解決辦法：
		// 1）、新VO+新的Repository+自訂類型轉換器
		// 2）、自訂類型轉換器 多寫判斷。相容於更多表類型
		System.out.println("bookRepostory.findById(1L).block() = " + bookRepositories.findById(1L).block());

		System.out.println("================");

//		System.out.println("bookAuthorRepostory.hahaBook(1L).block() = " + bookAuthorRepostory.hahaBook(1L).block());
		// 1-1：第二種方式
		
		databaseClient.sql("select b.*,t.name as name from t_book b LEFT JOIN t_author t on b.author_id = t.id WHERE b.id = ?")
			.bind(0, 1L)
			.fetch()
			.all()
			.map(row -> {
                String id = row.get("id").toString();
                String title = row.get("title").toString();
                String author_id = row.get("author_id").toString();
                String name = row.get("name").toString();
                TBook tBook = new TBook();

                tBook.setId(Long.parseLong(id));
                tBook.setTitle(title);

                TAuthor tAuthor = new TAuthor();
                tAuthor.setName(name);
                tAuthor.setId(Long.parseLong(author_id));

                tBook.setAuthor(tAuthor);

                return tBook;
            }).subscribe(tBook -> System.out.println("databaseClient tBook = " + tBook));
		

		// buffer api: 實作一對N；

		// 兩種辦法：
		// 1、一次查詢出來，封裝好
		// 2、兩次查詢

		// 1-N： 一個作者；可以查詢到很多圖書

		System.in.read();
	}

	@Test
	void authorRepositories() throws IOException {

		// 1、預設CRUD
		authorRepositories.findById(1L).subscribe(tAuthor -> System.out.println("findById tAuthor = " + tAuthor));

		// 2、QBC
		authorRepositories.findAllByIdInAndNameLike(java.util.Arrays.asList(1L, 2L, 3L), "%張%")
				.subscribe(tAuthor -> System.out.println("findAllByIdInAndNameLike tAuthor = " + tAuthor));

		// 3、自訂SQL
		authorRepositories.findHaha().subscribe(tAuthor -> System.out.println("findHaha tAuthor = " + tAuthor));

		System.in.read();
	}

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
					return new TAuthor(Long.parseLong(id), name,null);
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

//	@Test
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
						return new TAuthor(id, name,null);
					});
				}).subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

		// 背壓； 不用回傳所有東西，基於請求量回傳；

		System.in.read();

	}

}
