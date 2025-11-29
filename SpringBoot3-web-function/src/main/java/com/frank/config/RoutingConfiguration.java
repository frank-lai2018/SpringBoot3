package com.frank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import com.frank.biz.UserBizHandler;

/**
 * 場景：User RESTful - CRUD ● GET /user/1 取得1號用戶 ● GET /users 取得所有用戶 ●POST /user
 * 請求體攜帶JSON，新增一個用戶 ● PUT /user/1 請求體攜帶JSON，修改1號用戶 ● DELETE /user/1 刪除1號用戶
 */
@Configuration
public class RoutingConfiguration {


	/**
	 * 函數式Web： 1.在容器中放一個Bean：類型是 RouterFunction<ServerResponse>，集中所有路由資訊
	 * 2、每個業務準備一個自己的Handler
	 *
	 *
	 * 核心四大對象 1、RouterFunction： 定義路由資訊。發什麼請求，誰來處理
	 * 2、RequestPredicate：定義請求規則：請求謂語。請求方式（GET、POST）、請求參數 3、ServerRequest： 封裝請求完整數據
	 * 4、ServerResponse： 封裝回應完整數據
	 */
	@Bean
	public RouterFunction<ServerResponse> userRoute(UserBizHandler userBizHandler/* 這個會被自動注入進來，spring 提供的功能 */) {

		return RouterFunctions.route() // 開始定義路由資訊
				.GET("/user/{id}", RequestPredicates.accept(MediaType.ALL), userBizHandler::getUser)
				.GET("/users", userBizHandler::getUsers)
				.POST("/user", RequestPredicates.accept(MediaType.APPLICATION_JSON), userBizHandler::saveUser)
				.PUT("/user/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), userBizHandler::updateUser)
				.DELETE("/user/{id}", userBizHandler::deleteUser).build();
	}
}
