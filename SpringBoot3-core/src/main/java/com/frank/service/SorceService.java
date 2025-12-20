package com.frank.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.frank.dto.UserDTO;
import com.frank.event.LoginSuccessEvent;

@Service
public class SorceService {

	// 使用@EventListener方式來監聽事件，可以在類別中監聽多個事件，參數就是要監聽的事件
	@EventListener
	public void envetSource(LoginSuccessEvent event) {
		System.out.println("===== SorceService 收到事件 =====");
		UserDTO user = (UserDTO) event.getSource();
		sendSorce(user);
	}
	
	public void sendSorce(UserDTO user) {
		System.out.println("用戶登入，獲得10點積分 user=" + user);
	}
}
