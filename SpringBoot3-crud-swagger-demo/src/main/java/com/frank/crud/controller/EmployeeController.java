package com.frank.crud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.frank.crud.entity.Employee;
import com.frank.crud.service.EmployeeService;

import java.util.List;

@Tag(name = "員工", description = "員工CRUD")
@RestController
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@GetMapping("/emp/{id}")
	public Employee getEmployee(@PathVariable("id") Long id) {
		return employeeService.getEmployeeById(id);
	}

	@GetMapping("/emps")
	public List<Employee> getEmployee() {
		return employeeService.getEmployees();
	}

	@PostMapping("/emp")
	public String saveEmployee(@RequestBody Employee employee) {
		employeeService.saveEmployee(employee);
		return "ok";
	}

	@DeleteMapping("/emp/{id}")
	public String deleteEmployee(@PathVariable("id") Long id) {
		employeeService.deleteEmployee(id);
		return "ok";
	}
}