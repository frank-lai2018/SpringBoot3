package com.example.demo.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import com.example.demo.component.MyHahaComponent;

/**
 *
 * 1、實作 HealthIndicator 介面來客製化元件的健康狀態物件（Health） 返回 2、
 */
@Component
public class MyHahaHealthIndicator extends AbstractHealthIndicator {

	@Autowired
	MyHahaComponent myHahaComponent;

	/**
	 * 健康檢查
	 * 
	 * @param builder
	 * @throws Exception
	 */
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		// 自訂檢查方法

		int check = myHahaComponent.check();
		if (check == 1) {
			// 存活
			builder.up().withDetail("code", "1000").withDetail("msg", "活的很健康").withDetail("data", "我的名字叫haha").build();
		} else {
			// 下線
			builder.down().withDetail("code", "1001").withDetail("msg", "死的很健康").withDetail("data", "我的名字叫haha完蛋")
					.build();
		}

	}
}
