package com.frank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frank.service.UserService;

@RestController
public class DemoController {
	
	@Autowired
	private UserService userService;

	@GetMapping("/hello")
	public String hello() {
		return "Hello, World!";
	}
	
	@PostMapping("/getUser")
	public String getUser() {
		return userService.getUser();
	}
}
