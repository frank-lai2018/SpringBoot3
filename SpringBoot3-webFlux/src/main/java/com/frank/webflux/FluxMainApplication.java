package com.frank.webflux;

import java.io.IOException;
import java.net.URI;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

public class FluxMainApplication {
	public static void aa(String[] args) throws IOException {
		// 快速自己寫一個能處理請求的伺服器

		// 1、創建一個能處理Http請求的處理器。 參數：請求、回應； 回傳值：Mono<Void>：代表處理完成的訊號
		HttpHandler handler = (ServerHttpRequest request, ServerHttpResponse response) -> {
			URI uri = request.getURI();
			System.out.println(Thread.currentThread() + "請求進來：" + uri);
			// 編寫請求處理的業務,給瀏覽器寫一個內容 URL + "Hello~!"
			// response.getHeaders(); //取得回應頭
			// response.getCookies(); //取得Cookie
			// response.getStatusCode(); //取得回應狀態碼；
			// response.bufferFactory(); //buffer工廠
			// response.writeWith() //把xxx寫出去
			// response.setComplete(); //回應結束

			// 資料的發布者：Mono<DataBuffer>、Flux<DataBuffer>

			// 建立 回應資料的 DataBuffer
			DataBufferFactory factory = response.bufferFactory();

			// 數據Buffer
			DataBuffer buffer = factory.wrap(new String(uri.toString() + " ==> Hello!").getBytes());

			// 需要一個 DataBuffer 的發布者
			return response.writeWith(Mono.just(buffer));
//			return Mono.empty();
		};

		// 2、啟動一個伺服器，監聽8080端口，接受數據，拿到數據交給 HttpHandler 進行請求處理
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);

		// 3、啟動Netty伺服器
		HttpServer.create()
				.host("localhost")
				.port(8080)
				.handle(adapter) // 用指定的處理器處理請求
				.bindNow(); // 現在就綁定

		System.out.println("伺服器啟動完成....監聽8080，接受請求");
		System.in.read();
		System.out.println("伺服器停止....");
	}
}
