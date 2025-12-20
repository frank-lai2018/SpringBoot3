package com.frank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.frank.properties.RobotProperties;

@Controller
public class HelloController {

	@Autowired
	private RobotProperties robotProperties;
	
	@RequestMapping("/hello")
	@ResponseBody
	public String sayHello() {
        return "Hello +"+robotProperties.getName()+"+ , age="+robotProperties.getAge()+" , email="+robotProperties.getEmail();
    }
}
