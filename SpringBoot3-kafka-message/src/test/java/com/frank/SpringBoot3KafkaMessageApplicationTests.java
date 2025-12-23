package com.frank;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StopWatch;

import com.frank.entity.Person;

@SpringBootTest
class SpringBoot3KafkaMessageApplicationTests {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	@Test
	void contextLoads() {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		CompletableFuture[] futures = new CompletableFuture[10000];
		for (int i = 0; i < 10000; i++) {
			CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("news", "hihi", "hello kafka~~~" + i);
			futures[i] = future;
		}
		
		CompletableFuture.allOf(futures).join();
		
		System.out.println("All messages sent.");
		stopwatch.stop();
		System.out.println("Total time: " + stopwatch.getTotalTimeMillis() + " ms");
	}
	
	@Test
	void sendObject() {
		Person person = new Person(1l, "Frank", "frank_lai@xxxx.xxx");
		
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("news", "person2", person.toString());
		
		future.join();
		
		System.out.println("Person message sent.");
	}

}
