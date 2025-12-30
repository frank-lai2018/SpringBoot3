package com.frank.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        System.out.println("請求處理放行到目標方法之前...");
        Mono<Void> filter = chain.filter(exchange); //放行


        //流一旦經過某個操作就會變成新流

		Mono<Void> voidMono = filter.doOnError(err -> {
								System.out.println("目標方法異常以後...");
									}) // 目標方法發生異常後做事
									.doFinally(signalType -> {
										System.out.println("目標方法執行以後...");
									});// 目標方法執行後
										// 上面執行不花時間。
       return voidMono; //看清楚回傳的是誰 ！ ！ ！,一定要返回操作過的新流，不能返回還未操作的的filter
    }
}
