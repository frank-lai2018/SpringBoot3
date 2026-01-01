package com.frank.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Date;

/**
 */
@Table("t_book")
@Data
public class TBook {

    @Id
    private Long id;
    private String title;
    private Long authorId;
    private Instant publishTime; //響應式中日期的對應用 Instant 或 LocalXxx


    private TAuthor author; //每本書有唯一作者；

}
