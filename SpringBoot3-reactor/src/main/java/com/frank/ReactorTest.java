package com.frank;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscription;

import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ReactorTest {
	
	
	public static void main(String[] args) {
		new ReactorTest().thread();
	}
	
	public void thread() {
		
		//響應式：響應式程式設計： 全非同步、訊息、事件回調
		//預設還是用當前線程，產生整個流、發布流、流操作
		
		// 流的發布、中間操作，預設使用當前線程
		Flux.range(1, 10)
			.publishOn(Schedulers.immediate()) // 切換發佈者的線程
		    .log()
		    .map(integer -> integer + 10)
		    .log()
//		    .subscribeOn(Schedulers.boundedElastic()) // 切換訂閱者的線程   到彈性線程池
		    .subscribe();
		
		Schedulers.immediate(); // 默認: 無執行上下文  當前線程運行所有操作
		Schedulers.single(); // 使用固定一個單線程
		Schedulers.boundedElastic(); // 彈性線程池，適合阻塞操作， 有界的，不是無限擴充的線城池，線程池中有10-CPU的核心線程 隊列默認200k
		Schedulers.fromExecutor(new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000))); // 自定義線程池
		Schedulers.parallel(); // 預設線程數 = CPU核心數，適合CPU密集型任務
		
		
	}

	// Flux 處理器：handle 自訂議處理規則
	public void handle() {
		Flux.range(1, 10).handle((value, sink) -> {
			System.out.println("拿到的值：" + value);
			//這裡可以寫一些業務邏輯，例如用value去查資料庫，把某筆資料拿出來，再向下傳送下去
			
			sink.next("張三：" + value); // 可以向下傳送資料的通道
		}).log() // 日誌
				.subscribe();
	}
	
	
	
	public void create() throws InterruptedException {
	    MyListener listener = new MyListener();
//	    每 1 秒啟動一個線程，模擬用戶登錄。
//	    每個線程觸發 MyListener 的 online 方法，打印對應的用戶登錄信息。
	    for (int i = 0; i < 10; i++) {
	        int finalI = i;
	        new Thread(() -> {
	            listener.online("用戶-" + finalI);
	        }).start();
	        Thread.sleep(1000);
	    }
	    
	    //現在有個需求，每上線一個用戶給我一個用戶名，我要把這些用戶名組成一個數據流，然後進行處理，比如打印出來。
		Flux<String> userFlux = Flux.create(sink -> {
			for (int i = 0; i < 10; i++) {
				int finalI = i;
				new Thread(() -> {
					String userName = "用戶-" + finalI;
					sink.next(userName); // 發送用戶名到數據流
				}).start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					sink.error(e); // 發送錯誤信號
				}
			}
			sink.complete(); // 完成數據流
		});
	}

	class MyListener {
	    // 使用者登入，觸發online監聽
	    public void online(String userName) {
	        System.out.println("用戶登入了: " + userName);
	    }
	}
	
	
	
	// 編程方式創建序列
	// Sink：接收器、水槽、通道；
	// Source：數據源，Sink：接受端

	public void generate() {
	    Flux<Object> flux = Flux.generate(
	        () -> 0, // 初始state值
	        (state, sink) -> {
	            // 0-10
	            if (state <= 10) {
	                sink.next(state); // 把元素傳出去
	            } else {
	                sink.complete(); // 完成，完成信號
	            }

	            if (state == 7) {
	                sink.error(new RuntimeException("我不喜歡7"));
	            }
	            
	            return state + 1; // 返回新的迭代state值
	        }
	    );

	    flux.log()
	        .doOnError(throwable -> System.out.println("throwable = " + throwable))
	        .subscribe();
	}
	
	public void limit() {
	    Flux.range(1, 1000)
	        .log()//限流觸發後，看上游是怎麼限流獲取數據的
	        .limitRate(100) // 一次預取100個元素
	        .subscribe();

	    // 75% 預取策略：limitRate(100)
	    // 第一次抓取100個數據，如果 75% 的元素已經處理了，继续抓取 新的 75% 元素；
	}
	
	public void buffer() {
		Flux<List<Integer>> flux = Flux.range(1, 10) //原始流10個
				.buffer(3)
				.log();//緩衝區：緩衝3個元素: 消費一次最多可以拿到三個元素； 湊滿數批量發給消	費者
				// //一次發一個，一個一個發；
				// 10元素，buffer(3)；消費者請求4次，資料消費完成
		
		flux.subscribe(System.out::println);
	}
	
	public void dispose() {
	    Flux<Integer> flux = Flux.range(1, 10000)
	        .delayElements(Duration.ofSeconds(1))
	        .map(i -> i + 7)
	        .log();

	    // 1、消費者是實現了 Disposable 可取消
	    Disposable disposable = flux.subscribe(System.out::println);

	    new Thread(() -> {
	        try {
	            Thread.sleep(10000);
	            disposable.dispose(); // 銷毀
	        } catch (InterruptedException e) {
	        }
	    }).start();
	}
	
	public void subscribe(String[] args) {
		
		// onErrorXxx、doOnXxxx
		// doOnXxx： 發生這個事件的时候產生一個回調，通知你（不能改變）；
		// onXxx： 發生這個事件后執行一個動作，可以改變元素、信號
		// AOP：普通通知（前置、後置、异常、返回） 環繞通知（ProceedingJoinPoint）
		
		
		Flux<String> flux = Flux.just(1,2,3,4,5,6,7,8,9)
		.map(i -> {
			System.out.println("map處理元素：" + i);
			if(i == 9) {
				i = 10/(9-i); // 故意製造異常
			}
			return "哈哈-" + i;
			})
		.onErrorComplete();//發生異常後，完成流（正常結束），把錯誤異常吃掉，轉為正常訊號
		
		flux.subscribe();//流被訂閱;默認訂閱
		flux.subscribe(System.out::println);//指定訂閱規則:正常消費者 只消費正常元素
		flux.subscribe(
				value -> System.out.println("接收到的值：" + value), // 流元素消費
				error -> System.err.println("發生錯誤：" + error),// 感知異常結束
				() -> System.out.println("數據流完成"));// 感知正常結束
		
		//流的生命週期鉤子，可以傳撥給訂閱者
		flux.subscribe(new BaseSubscriber<Object>() {
			
			
			//生命週期鉤子1: 訂閱關係綁定的時候觸發
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				//流被訂閱的時候觸發
				System.out.println("訂閱關係綁定好了：" + subscription);
				
				//找發布者要一個元素
				request(1); // 要求一個數據
//				requestUnbounded(); // 不限量要求數據
			}

			@Override
			protected void hookOnNext(Object value) {
				System.out.println("數據到達：" + value);
				
				if ("哈哈-7".equals(value)) {
					// 取消訂閱
					cancel();
					
				}
				
				request(1); // 要求一個數據
			}

			@Override
			protected void hookOnComplete() {
				System.out.println("數據流正常結束");
			}

			@Override
			protected void hookOnError(Throwable throwable) {
				System.out.println("數據流異常結束：" + throwable);
			}

			@Override
			protected void hookOnCancel() {
				System.out.println("數據流被取消");
			}

			@Override
			protected void hookFinally(SignalType type) {
				System.out.println("數據流最終結束，不管異常或失敗一定會被執行，結束類型：" + type);
			}

		});
	}

}
