package com.frank.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

	// WebFlux： 向下相容原來SpringMVC的大多數註解和API；
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", required = false, defaultValue = "no key") String name) {
		return "Hello WebFlux! name=" + name;
	}

	// 現在推薦的方式
	// 1、回傳單一資料Mono： Mono<Order>、User、String、Map
	// 2、傳回多個資料Flux： Flux<Order>
	// 3、配合Flux，完成SSE： Server Send Event；服務端事件推播

	@GetMapping("/haha")
	public Flux<String> haha​​() {

		// ResponseEntity.status(305)
		// .header("aaa","bbb")
		// .contentType(MediaType.APPLICATION_CBOR)
		// .body("aaaa")
		// .

//		return Mono.just(0).map(i -> 10 / i).map(i -> "哈哈-" + i);
		return Flux.just("哈哈1", "哈哈2", "哈哈3");
	}

	// text/event-stream
	// SSE測試； chatgpt都在用； 服務端推送
//	@GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//	public Flux<String> sse() {
//		return Flux
//				.range(1, 10).map(i -> "ha-" + i)
//				.delayElements(Duration.ofMillis(500));
//	}
	@GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> sse() {
		return Flux
				.range(1, 10).map(i -> {
					// 建構一個SSE對象
					return ServerSentEvent
							.builder("ha-" + i)
							.id(i + "")
							.comment("hei-" + i)
							.event("haha")
							.build();
				})
				.delayElements(Duration.ofMillis(500));
	}

	// SpringMVC 以前怎麼用，基本上可以無縫切換。
	// 底層：需要自己開始寫響應式程式碼
}
