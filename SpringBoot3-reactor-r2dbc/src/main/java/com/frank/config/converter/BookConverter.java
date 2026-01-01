package com.frank.config.converter;

import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.frank.dto.TAuthor;
import com.frank.dto.TBook;
import com.frank.dto.TBookAuthor;

import java.time.Instant;

/*

*
* 告訴Spring Data 怎麼封裝Book對象
*/
@ReadingConverter // 讀取資料庫資料的時候,把row轉成 TBook
public class BookConverter implements Converter<Row, TBook> {

	//1）、@Query 指定了 sql如何傳送
	//2)、自訂 BookConverter 指定了 資料庫傳回的一 Row 數據，怎麼封裝成 TBook
	//3）、設定 R2dbcCustomConversions 元件，讓 BookConverter 加入其中生效
	@Override
	public TBook convert(Row source) {
		if (source == null)
			return null;
		//自訂結果集的封裝
		TBook tBook = new TBook();

		tBook.setId(source.get("id", Long.class));
		tBook.setTitle(source.get("title", String.class));

		Long author_id = source.get("author_id", Long.class);
		tBook.setAuthorId(author_id);
		tBook.setPublishTime(source.get("publish_time", Instant.class));

		//讓 converter相容於更多的表結構處理
		if (source.getMetadata().contains("name")) {
			TAuthor tAuthor = new TAuthor();
			tAuthor.setId(author_id);
			tAuthor.setName(source.get("name", String.class));

			tBook.setAuthor(tAuthor);
		}

		return tBook;
	}
}
