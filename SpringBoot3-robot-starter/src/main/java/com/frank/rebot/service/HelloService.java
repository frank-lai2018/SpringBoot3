package com.frank.rebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.frank.rebot.config.RobotProperties;

@Service
public class HelloService {

	@Autowired
	private RobotProperties robotProperties;
	
	public String sayHello() {
		return "Hi, " + robotProperties.getName() + " ,age="+robotProperties.getAge()+", email="+robotProperties.getEmail();
	}
}
