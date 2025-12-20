package com.frank.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.frank.dto.UserDTO;
import com.frank.event.LoginSuccessEvent;

@Service
public class LogService {
	
	// 使用@EventListener方式來監聽事件，可以在類別中監聽多個事件，參數就是要監聽的事件
	@EventListener
	public void eventLogInfo(LoginSuccessEvent event) throws InterruptedException {
		System.out.println("===== LogService 收到事件 =====");
		UserDTO user = (UserDTO) event.getSource();
		logInfo(user);
	}

	public void logInfo(UserDTO user) throws InterruptedException {
		Thread.sleep(10000);
		System.out.println("記錄日誌: user=" + user);
	}
}
