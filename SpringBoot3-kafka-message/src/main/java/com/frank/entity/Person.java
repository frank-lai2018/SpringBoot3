package com.frank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person implements java.io.Serializable {
	private static final long serialVersionUID = -8344402193195555542L;
	private Long id;
	private String name;
	private String email;
}
