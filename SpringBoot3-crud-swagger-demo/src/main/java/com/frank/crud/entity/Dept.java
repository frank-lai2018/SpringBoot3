package com.frank.crud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(title = "部門資訊")
@Data
public class Dept {

	@Schema(title = "部門id")
	private Long id;
	@Schema(title = "部門名字")
	private String deptName;
}