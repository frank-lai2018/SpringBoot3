package com.frank.service;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.frank.dto.UserDTO;
import com.frank.event.LoginSuccessEvent;

/**
 * 使用繼承方式來監聽事件
 * */
@Service
public class DiscountService implements ApplicationListener<LoginSuccessEvent> {
	
	public void sendDiscount(UserDTO user) throws InterruptedException {
		Thread.sleep(10000); // 模擬耗時的業務處理
		System.out.println("用戶登入，獲得1張折價 user="+user);
		
	}

	@Override
	public void onApplicationEvent(LoginSuccessEvent event) {
		 System.out.println("=====  DiscountService  收到事件 =====");
		 UserDTO user= (UserDTO) event.getSource();
		 try {
			sendDiscount(user);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
