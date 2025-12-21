package com.frank.crud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.frank.crud.entity.Dept;
import com.frank.crud.service.DeptService;

import java.util.List;

@Tag(name = "部門", description = "部門的CRUD")
@RestController
public class DeptController {

	@Autowired
	DeptService deptService;

	// Knife4j
	@Operation(summary = "查詢", description = "依照id查詢部門")
	@GetMapping("/dept/{id}")
	public Dept getDept(@PathVariable("id") Long id) {
		return deptService.getDeptById(id);
	}

	@Operation(summary = "查詢所有部門")
	@GetMapping("/depts")
	public List<Dept> getDept() {
		return deptService.getDepts();
	}

	@Operation(summary = "保存部門", description = "必須提交json")
	@PostMapping("/dept")
	public String saveDept(@RequestBody Dept dept) {
		deptService.saveDept(dept);
		return "ok";
	}

	@Operation(summary = "依照id刪除部門", description = "必須提交json")
	@DeleteMapping("/dept/{id}")
	public String deleteDept(@PathVariable("id") @Parameter(description = "部門id") Long id) {
		deptService.deleteDept(id);
		return "ok";
	}
}