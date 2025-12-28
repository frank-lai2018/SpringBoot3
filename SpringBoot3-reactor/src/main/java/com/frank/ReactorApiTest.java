package com.frank;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

public class ReactorApiTest {

	public static void main(String[] args) throws Exception {
		new ReactorApiTest().threadlocal();
	}
	
	// Context-API： https://projectreactor.io/docs/core/release/reference/#context
	 //ThreadLocal在響應式程式設計中無法使用。 
	//響應式中，資料流期間共享數據，Context API: Context：讀寫 ContextView：只讀；
	void threadlocal() {

		// 支援Context的中間操作
		Flux.just(1, 2, 3).transformDeferredContextual((flux, context) -> {
			System.out.println("flux = " + flux);
			System.out.println("context = " + context);
			return flux.map(i -> i + "==>" + context.get("haha"));
		})
				// 上游能拿到下游的最近一次數據
				.contextWrite(ctx -> ctx.put("haha", "HELLO"))
				// ThreadLocal共享了數據，上游的所有人能看到; Context由下游傳播給上游
				.subscribe(v -> System.out.println("v = " + v));
	}
	
	
	void paralleFlux() throws IOException {
		// 百萬數據，8個線程，每個線程處理100，進行分批處理一直處理結束

		Flux.range(1, 1000000)
				.buffer(100)//分批100個元素進行處理
				.parallel(8)//使用8個線程並行處理
				.runOn(Schedulers.newParallel("yy"))//指定線程池
				.log()
				.flatMap(list -> Flux.fromIterable(list))
				.collectSortedList(Integer::compareTo)
				.subscribe(v -> System.out.println("v = " + v));

		System.in.read();
	}
	
	//以前 命令式編程
	// controller -- service -- dao 參數重controller往下傳遞到service到dao
	 //響應式程式設計 dao(10：資料來源) --> service(10) --> controller(10); 從下游反向傳播，dao去撈數據發佈,service訂閱完dao消費後再發佈，controller訂閱service消費數據，所以參數要由下游反向傳播

	void block() {
		//
		// Integer integer = Flux.just(1, 2, 4)
		// .map(i -> i + 10)
		// .blockLast();
		// System.out.println(integer);

		List<Integer> integers = 
				Flux.just(1, 2, 4)
				.map(i -> i + 10)
				.collectList() // 收集成集合  拿到 Mono<List<Integer>> collectList =
				.block(); // 也是一種訂閱者； 拿到 List<Integer> integers
		
		
																							// BlockingMonoSubscriber

		System.out.println("integers = " + integers);
	}
	
	void sinks() throws InterruptedException, IOException {

		// Flux.create(fluxSink -> {
		// fluxSink.next("111")
		// })

		 Sinks.many(); //傳送Flux資料。
		// Sinks.one(); //傳送Mono數據

		// Sinks： 接受器，資料管道，所有資料都順著這個管道往下走的

		// Sinks.many().unicast(); //單播： 這個管道只能綁定單一訂閱者（消費者）
		// Sinks.many().multicast();//多重播放： 這個管道能綁定多個訂閱者
		// Sinks.many().replay();//重播： 這個管道能重播元素。 是否給後來的訂閱者把之前的元素依然發給它；

		// 從頭消費還是從訂閱的那一刻消費；

		 Sinks.Many<Object> many = Sinks.many()
//				 .unicast() //單播
				 .multicast() //多播
				 .onBackpressureBuffer(5); //背壓隊列 發布者最多緩存訂閱者來不及消費的元素數量 超過就報錯；

		// 預設訂閱者，從訂閱的那一刻開始接元素

		// 發布者資料重播； 底層利用隊列進行快取之前數據； 
//		 Sinks.Many<Object> many = Sinks.many().replay().limit(3);
		//
		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				many.tryEmitNext("a-" + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		//
		//
		//
		// //訂閱
		 many.asFlux().subscribe(v-> System.out.println("v1 = " + v));
		//
		new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			many.asFlux().subscribe(v -> System.out.println("v2 = " + v));
		}).start();

//		Flux<Integer> cache = Flux.range(1, 10).delayElements(Duration.ofSeconds(1)) // 不調快取預設就是快取所有
//				.cache(1); // 快取兩個元素； 預設全部快取
//
//		cache.subscribe();// 快取元素;

		// 最定義訂閱者
//		new Thread(() -> {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//			cache.subscribe(v -> System.out.println("v = " + v));
//		}).start();

		System.in.read();

	}
	

	 void retryAndTimeout() throws IOException {

		 Flux.just(1)
			 .delayElements(Duration.ofSeconds(3))
			 .log()
			 .timeout(Duration.ofSeconds(2))
			 .retry(2) // 把流從頭到尾重新請求一次
//			 .onErrorReturn(2)
			 .map(i-> i+"haha")
			 .subscribe(v-> System.out.println("v = " + v));
	
	
		 System.in.read();

	 }

	// 預設：錯誤是一種中斷行為；
	// subscribe: 消費者可以感知 正常元素try 與 流發生的錯誤catch ;
	// 更多的錯誤處理：
	// java 錯誤處理
	void error() throws IOException {

		
		 /**
		  * onErrorReturn 類似於以下命令式代碼
		  * try {
				return doSomethingDangerous(10);
			}
			catch (Throwable error) {
				return "RECOVERED";
			}
		  * 
		  * onErrorReturn: 實現上面效果，錯誤的時候回傳一個值
				●1. 吃掉異常，消費者無異常感知
				●2. 返回一個兜底預設值
				●3、 流正常完成；
		  * */
		
		 Flux.just(1, 2, 0, 4)
		 .map(i -> "100 / " + i + " = " + (100 / i))
		 .onErrorReturn("RECOVERED")
		 .subscribe(v -> System.out.println("v = " + v),
		 err -> System.out.println("err = " + err),
		 () -> System.out.println("串流結束"));
		 
		 
		 /**
		  * onErrorReturn 類似於以下命令式代碼
		  * try {
				return doSomethingDangerous(10);
			}
			catch (NullPointerException error) {
				return "RECOVERED";
			}
		  * 
		  * 
		  * */
		 
		 Flux.just(1, 2, 0, 4)
		 .map(i -> "100 / " + i + " = " + (100 / i))
		 .onErrorReturn(NullPointerException.class,"RECOVERED")
		 .subscribe(v-> System.out.println("v = " + v),
				 err -> System.out.println("err = " + err),
				 ()-> System.out.println("流結束")); // error handling example
		 
		 
		 /**
		  * onErrorResume類似於以下命令式代碼
			  * try {
					return doSomethingDangerous(10);
				}
				catch (Throwable error) {
					return haha​​ha(10);
				}
				
			*onErrorResume:
				●1. 吃掉異常，消費者無異常感知
				●2. 呼叫一個兜底方法
				●3. 流正常完成	
		  * */
		 
		  Flux.just(1, 2, 0, 4)
			  .map(i -> "100 / " + i + " = " + (100 / i))
			  .onErrorResume(err -> haha​​ha(err))
			  .subscribe(v -> System.out.println("v = " + v),
					  err -> System.out.println("err = " + err),
					  () -> System.out.println("串流結束"));

		  /**
		   * try {
					return callExternalService(k);
				}
				catch (Throwable error) {
					throw new BusinessException("oops, SLA exceeded", error);
				}
				
				包裝重新拋出異常: 推薦用 .onErrorMap 方法2
					●1. 吃掉異常，消費者有感知
					● 2、拋新異常
					●3. 流異常完成
		   * */
		 //方法1
		 Flux.just(1, 2, 0, 4)
			 .map(i -> "100 / " + i + " = " + (100 / i))
			 .onErrorResume(err -> Flux.error(new BusinessException(err.getMessage()+"：炸了")))
			 .subscribe(v -> System.out.println("v = " + v),
			 err -> System.out.println("err = " + err),
			 () -> System.out.println("串流結束"));
		 
		//方法2
		 Flux.just(1, 2, 0, 4)
		 .map(i -> "100 / " + i + " = " + (100 / i))
		 .onErrorMap(err-> new BusinessException(err.getMessage()+": 又炸了..."))
		 .subscribe(v -> System.out.println("v = " + v),
					 err -> System.out.println("err = " + err),
					 () -> System.out.println("串流結束"));

		 
		 /**
		  * try {
				return callExternalService(k);
			}
			catch (RuntimeException error) {
				//make a record of the error
				System.out.println("串流訊號："+signalType);
				throw error;
			}finally{
				System.out.println("串流訊號：" + signalType);
			}	
		  * 
		  * ***/
			Flux.just(1, 2, 3, 4)
			.map(i -> "100 / " + i + " = " + (100 / i))
			.doOnError(err -> {
							System.out.println("err已被記錄 = " + err);
						})
			.doFinally(signalType -> {
							System.out.println("串流訊號：" + signalType);
						})
			.subscribe(v -> System.out.println("v = " + v), 
					err -> System.out.println("err = " + err),
					() -> System.out.println("串流結束"));

		/**
		 * onErrorContinue
		 * 一旦某個元素處理發生錯誤，就跳過這個元素，繼續處理下一個元素
		 * **/
		 Flux.just(1,2,3,0,5)
			 .map(i->10/i)
			 .onErrorContinue((err,val)->{
				 System.out.println("err = " + err);
				 System.out.println("val = " + val);
				 System.out.println("發現"+val+"有問題了，繼續執行其他的，我會記錄這個問題");
			 	}) //發生
			 .subscribe(v-> System.out.println("v = " + v),
			 err-> System.out.println("err = " + err));

		 
		/**
		 * 測試onErrorStop、onErrorComplete
		 * 
		 * */ 
		Flux<Long> map = Flux.interval(Duration.ofSeconds(1)).map(i -> 10 / (i - 10));

		map
		//.onErrorStop() // 錯誤後停止流. 源頭中斷，所有監聽者全部結束; 錯誤結束
		 .onErrorComplete() //把錯誤結束訊號，替換為正常結束訊號； 正常結束
				.subscribe(v -> System.out.println("v = " + v), 
						err -> System.out.println("err = " + err),
						() -> System.out.println("流正常結束"));

		map.subscribe(v -> System.out.println("v1 = " + v), 
				err -> System.out.println("err1 = " + err),
				() -> System.out.println("流正常結束1"));

		System.in.read();

	}

	class BusinessException extends RuntimeException {
		public BusinessException(String msg) {
			super(msg);
		}
	}

	Mono<String> haha​​ha(Throwable throwable) {
		if (throwable.getClass() == NullPointerException.class) {

		}

		return Mono.just("哈哈-" + throwable.getMessage());
	}
	

	/**
	 * zip: 無法配對的元素會被忽略； 最多支援8流壓縮；
	 */
	void zip() {
		// Tuple：元組；
		// Flux< Tuple2:<Integer,String> >

		// Flux.zip(Flux.just(1,2),Flux.just(1,2),Flux.just(2,3),Flux.just(1))
		// .map(tuple->{
		// tuple.get
		// })

		Flux.just(1, 2, 3)
			.zipWith(Flux.just("a", "b", "c", "d"))
			.map(tuple -> {
					Integer t1 = tuple.getT1(); // 元組中的第一個元素
					String t2 = tuple.getT2();// 元組中的第二個元素
					return t1 + "==>" + t2;
				})
			.log()
			.subscribe(v -> System.out.println("v = " + v));

	}

	/**
	 * concat： 連接； A流 所有元素和 B流所有元素拼接 
	 * merge：合併； A流 所有元素和 B流所有元素 依照時間序列合併 
	 * mergeWith：
	 * mergeSequential： 依照哪個流先發元素排隊
	 */
	void merge() throws IOException {

		Flux.mergeSequential();

		Flux.merge(Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)),
				Flux.just("a", "b").delayElements(Duration.ofMillis(1500)),
				Flux.just("haha", "hehe", "heihei", "xixi").delayElements(Duration.ofMillis(500)))
			.log()
			.subscribe();

		Flux.just(1, 2, 3).mergeWith(Flux.just(4, 5, 6));

		System.in.read();
	}

	/**
	 * defaultIfEmpty: 靜態兜底數據 
	 * switchIfEmpty: 空轉換； 呼叫動態兜底方法； 返回新流數據
	 */
	void empty() {
		// Mono.just(null);//流裡面有一個null值元素
		// Mono.empty();//流裡面沒有元素，只有完成訊號/結束訊號

		hehe()
		//.defaultIfEmpty("靜態兜底資料...")// 如果發布者元素為null，指定預設值，否則用發布者的值；
		.switchIfEmpty(hehe())// 如果發布者為空，呼叫方法取得新的發布者；
		.subscribe(v -> System.out.println("v = " + v));

	}

	Mono<String> hehe() {
		return Mono.just("兜底資料...");
	}

	Mono<String> haha​​() {
		return Mono.empty();
	}

	/**
	 * transform: transformDeferred: * * 、、、
	 */
	// 把流變形成新數據
	void transform() {

		AtomicInteger atomic = new AtomicInteger(0);

		Flux<String> flux = Flux.just("a", "b", "c").transformDeferred(values -> {
			// ++atomic
			if (atomic.incrementAndGet() == 1) {
				// 如果是：第一次調用，老流中的所有元素轉成大寫
				return values.map(String::toUpperCase);
			} else {
				// 如果不是第一次調用，原封不動返回
				return values;
			}
		});

		// transform 無defer，不會共用外部變數的值。 無狀態轉換; 原理，無論多少訂閱者，transform只會執行一次
		// transform 有defer，會共用外部變數的值。 有狀態轉換; 原理，無論多少訂閱者，每個訂閱者transform都只執行一次
		flux.subscribe(v -> System.out.println("訂閱者1：v = " + v));
		flux.subscribe(v -> System.out.println("訂閱者2：v = " + v));
	}

	/***
	 * concatMap： 一個元素可以 變很多單一； 對於元素類型無限制 concat： Flux.concat; 靜態調用 concatWith：
	 * 連接的流和老流中的元素類型要一樣
	 *
	 */
	void concatMap() {

		Flux.just(1, 2).concatWith(Flux.just(4, 5, 6)).log().subscribe();

		// 連接
		// Flux.concat(Flux.just(1,2),Flux.just("h","j"),Flux.just("haha","hehe"))
		// .log()
		// .subscribe();

		// Mono、FLux：發布者
		// Flux.just(1,2)
		// .concatMap(s-> Flux.just(s+"->a",1))
		// .log()
		// .subscribe();

	}

	/**
	 * flatMap、
	 */
	// 扁平化
	void flatrMap() {
		Flux.just("zhang san", "li si").flatMap(v -> {
			String[] s = v.split(" ");
			return Flux.fromArray(s); // 把資料包裝成多元素流
		}).log().subscribe();// 兩個人的名字，依照空格拆分，印出所有的姓與名
	}

	/**
	 * filter、 onSubscribe：流被訂閱 request(unbounded)：請求無限數據 onNext(2): 每個資料到達
	 * onNext(4): 每個資料達到 onComplete：流結束
	 */
	void filter() {

		Flux.just(1, 2, 3, 4) // 串流發布者
				.log() // 1,2,3,4
				.filter(s -> s % 2 == 0) // 過濾偶數, 消費上面的流，request(1); request(1);
				// .log() // 2,4
				.subscribe(); // 最終消費者;
	}
}
