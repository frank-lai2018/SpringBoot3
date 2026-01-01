package com.frank.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.frank.dto.TAuthor;

import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

/**
 */
@Repository
public interface AuthorRepositories extends R2dbcRepository<TAuthor,Long> {

	//預設繼承了一堆CRUD方法；像mybatis-plus

	 //QBC： Query By Criteria
	 //QBE： Query By Example

	 //成為一個命名工程師 where id In () and name like ?
	 //僅限單表複雜條件查詢
	 Flux<TAuthor> findAllByIdInAndNameLike(List<Long> id, String name);

	 //多表複雜查詢

	 @Query("select * from t_author") //自訂query註解，指定sql語句
	 Flux<TAuthor> findHaha();


	 // 1-1：關聯
	 // 1-N：關聯
	 //場景：
	 // 1. 一本圖書有唯一作者；1​​-1
	 // 2.一個作者可以有很多書： 1-N


}
