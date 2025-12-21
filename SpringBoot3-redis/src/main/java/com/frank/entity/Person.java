package com.frank.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person implements Serializable {

	private static final long serialVersionUID = -8019199599444886082L;
	private Long id;
    private String name;
    private Integer age;
    private Date birthDay;
}