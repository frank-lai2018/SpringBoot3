package com.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.frank.dto.UserDTO;
import com.frank.event.EventPublisher;
import com.frank.event.LoginSuccessEvent;

@Service
public class LoginService {
	
	@Autowired
	private DiscountService discountService;
	@Autowired
	private LogService logService;
	@Autowired
	private SorceService sorceService;
	
	@Autowired
	private EventPublisher eventPublisher;

	public void login(String name, String pwd) {
		UserDTO user = new UserDTO();
		user.setName(name);
		user.setPwd(pwd);
		
		// 模擬登入成功後的業務處理 
		
		// 使用事件驅動方式，	將LoginSuccessEvent 事件發布出去
		LoginSuccessEvent event = new LoginSuccessEvent(user);
		this.eventPublisher.publishEvent(event);
		
		
//		//原本傳統寫法
//		// 記錄日誌
//		logService.logInfo(user);
//		// 發送user
//		sorceService.sendSorce(user);
//		// 發送折價券
//		discountService.sendDiscount(user);
		
		
		System.out.println("模擬登入成功 user=" + user);
	}
}
