package com.frank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frank.bean.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class XmlController {
	/**
	 * 1.預設支援把物件寫成json。因為預設web場景導入了jackson處理json的套件;jackson-core
	 * 2.jackson也支援把資料寫成xml。導入xml相關依賴
	 * 
	 * @return
	 */
	@GetMapping("/person")
	public Person person(/* @RequestBody Person person */) {
		Person person = new Person();
		person.setId(1L);
		person.setUserName("張三");
		person.setEmail("aaa@qq.com");
		person.setAge(18);
		return person;
	}
}
