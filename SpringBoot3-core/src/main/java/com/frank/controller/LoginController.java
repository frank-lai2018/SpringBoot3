package com.frank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.frank.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	public LoginService loginService;
	
	@RequestMapping("/login")
	@ResponseBody
	public String login(@RequestParam String name,@RequestParam String pwd) {
		loginService.login(name, pwd);
		return "success............";
	}
}
