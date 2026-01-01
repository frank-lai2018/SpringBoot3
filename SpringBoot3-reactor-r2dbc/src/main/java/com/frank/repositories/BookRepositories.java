package com.frank.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.frank.dto.TBook;

import reactor.core.publisher.Mono;

@Repository
public interface BookRepositories  extends R2dbcRepository<TBook,Long>{
	// // 1-1關聯關係； 查出這本圖書以及它的作者
	@Query("select b.*,t.name as name from t_book b" + " LEFT JOIN t_author t on b.author_id = t.id "
			+ " WHERE b.id = :bookId")
	Mono<TBook> haha​​Book(@Param("bookId") Long bookId);
}
