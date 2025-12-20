package com.frank.rebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.frank.rebot.service.HelloService;

@Controller
public class HelloControler {
	
	@Autowired
	private HelloService helloService;

	@ResponseBody
	@GetMapping("/hello")
	public String greet() {
		return helloService.sayHello();
	}
}
