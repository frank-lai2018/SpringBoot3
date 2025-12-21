package com.frank;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frank.entity.Person;

@RestController
public class RedisTestController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	//為了後來系統的相容性，應該所有物件都是以json的方式進行保存
	 @Autowired //如果給redis中保存資料會使用預設的序列化機制，導致redis中保存的物件不可視
	private RedisTemplate<Object,Object> redisTemplate;
	
	@RequestMapping("/hello")
	public String count() {
		Long hello = stringRedisTemplate.opsForValue().increment("hello");
		return "訪問了HELLO: " + hello + " 次";
	}
	
	@RequestMapping("/person/save")
	public String save() {
		Person person = new Person(1L,"張三",18,new Date());
		redisTemplate.opsForValue().set("person", person);
		return "OK";
	}
	
	@RequestMapping("/person/select")
	public Person select() {
		Person object = (Person)redisTemplate.opsForValue().get("person");
		return object;
	}
}
