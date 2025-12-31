package com.frank.dto;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table("t_author")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TAuthor {

    private Long id;
    private String name;

}
