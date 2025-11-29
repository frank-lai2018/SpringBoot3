package com.frank.biz;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.frank.bean.Person;

import jakarta.servlet.ServletException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class UserBizHandler {
	/**
	 * 查詢指定id的用戶
	 * 
	 * @param request
	 * @return
	 */
	public ServerResponse getUser(ServerRequest request) throws Exception {
		String id = request.pathVariable("id");
		log.info("查詢 【{}】 使用者訊息，資料庫正在檢索", id);
		// 業務處理
		Person person = new Person(1L, "哈哈", "aa@qq.com", 18, "admin");
		// 建構回應
		return ServerResponse.ok().body(person);
	}

	/**
	 * 取得所有用戶
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public ServerResponse getUsers(ServerRequest request) throws Exception {
		log.info("查詢所有使用者資訊完成");
		// 業務處理
		List<Person> list = Arrays.asList(new Person(1L, "哈哈", "aa@qq.com", 18, "admin"),
				new Person(2L, "哈哈2", "aa2@qq.com", 12, "admin2"));

		// 建構回應
		return ServerResponse.ok().body(list); // 凡是body中的對象，就是以前@ResponseBody原理。利用HttpMessageConverter 寫出為json
	}

	/**
	 * 儲存用戶
	 * 
	 * @param request
	 * @return
	 */
	public ServerResponse saveUser(ServerRequest request) throws ServletException, IOException {
		// 提取請求體
		Person body = request.body(Person.class);
		log.info("儲存使用者資訊：{}", body);
		return ServerResponse.ok().build();
	}

	/**
	 * 更新用戶
	 * 
	 * @param request
	 * @return
	 */
	public ServerResponse updateUser(ServerRequest request) throws ServletException, IOException {
		Person body = request.body(Person.class);
		log.info("儲存使用者資訊更新: {}", body);
		return ServerResponse.ok().build();
	}

	/**
	 * 刪除用戶
	 * 
	 * @param request
	 * @return
	 */
	public ServerResponse deleteUser(ServerRequest request) {
		String id = request.pathVariable("id");
		log.info("刪除【{}】使用者資訊", id);
		return ServerResponse.ok().build();
	}
}
