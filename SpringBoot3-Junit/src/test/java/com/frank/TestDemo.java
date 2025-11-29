package com.frank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.frank.controller.DemoController;


/**
 * 1.使用 @WebMvcTest 只載入 MVC 切片，不會建立 @Service Bean，導致 DemoController @Autowired 的 UserService 找不到。只能搭配@MockBean 模擬 Service 層。
 * 2.若要測試包含 Service 層的功能，應使用 @SpringBootTest 搭配 @AutoConfigureMockMvc。
 * */

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(DemoController.class) 
public class TestDemo {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getUserTest() throws Exception {
		ResultActions andExpect = mockMvc.perform(get("/hello")).andExpect(status().isOk());
		System.out.println(andExpect.andReturn().getResponse().getContentAsString());

	}
	
	@Test
	public void getPostUserTest() throws Exception {
		ResultActions andExpect = mockMvc.perform(post("/getUser")).andExpect(status().isOk());
		System.out.println(andExpect.andReturn().getResponse().getContentAsString());
		
	}

}
