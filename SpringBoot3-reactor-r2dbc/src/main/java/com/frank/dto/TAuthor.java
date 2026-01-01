package com.frank.dto;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table("t_author")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TAuthor {

	@Id
    private Long id;
    private String name;
    
  //1-N如何封裝
    @Transient //臨時字段，並不是資料庫表中的字段
   // @Field(exist=false)
    private List<TBook> books;

}
