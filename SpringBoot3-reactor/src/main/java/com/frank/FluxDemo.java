package com.frank;
import java.io.IOException;
import java.time.Duration;
import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;


/*
 * 因eclipse無法辨識reactor套件，故未放置於com.frank.reactor套件下
 * https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html
 * 
 * */
public class FluxDemo {

	public static void main(String[] args) throws IOException {
//		FluxDemo demo = new FluxDemo();
//		demo.doOnXxxx(args);
		
		// Flux.concat(Flux.just(1,2,3),Flux.just(7,8,9))
		// .subscribe(System.out::println);


		 Flux.range(1, 7)
		// .log() //日誌 onNext(1~7)
		 .filter(i -> i > 3) //挑出>3的元素
		// .log() //onNext(4~7)
		 .map(i -> "haha-" + i)
		 .log() // onNext(haha-4 ~ 7)
		 .subscribe(System.out::println);
	}

	/**
	 * 響應式程式設計核心：看懂文件彈珠圖； 訊號： 正常/異常（取消） 
	 * SignalType： 
	 * SUBSCRIBE： 被訂閱 
	 * REQUEST： 請求了N個元素
	 * CANCEL： 流被取消 
	 * ON_SUBSCRIBE：訂閱時候 
	 * ON_NEXT： 在元素到達 
	 * ON_ERROR： 在流錯誤
	 * ON_COMPLETE：在串流正常完成時 
	 * AFTER_TERMINATE：中斷以後 
	 * CURRENT_CONTEXT：目前上下文
	 * ON_CONTEXT：感知上下文
	 * <p>
	 * 事件感知API:當劉發生什麼事的時候，觸發一個回調，系統調用提前定義好的鉤子函數(Hook鉤子函數) doOnXxx API觸發時機
	 * 1、doOnNext：每個資料（流的資料）到達的時候觸發 
	 * 2、doOnEach：每個元素（流的資料和訊號）到達的時候觸發 
	 * 3、doOnRequest：消費者請求流元素的時候 
	 * 4、doOnError：流發生錯誤 5、doOnSubscribe: 流被訂閱的時候 6、doOnTerminate：
	 * 發送取消/異常訊號中斷了流 7、doOnCancle： 流被取消 8、doOnDiscard：流中元素被忽略的時候
	 *
	 * @param args
	 */
	public void doOnXxxx(String[] args) {

		// 關鍵：doOnNext：表示流中某個元素到達以後觸發我一個回調
		// doOnXxx要感知某個流的事件，寫在這個流的後面，新流的前面
		Flux.just(1, 2, 3, 4, 5, 6, 7, 0, 5, 6)
				.doOnNext(integer -> System.out.println("元素到達：" + integer)) // 元素到達得到時候觸發
				.map(integer -> 10 / integer) // 10,5,3,
				.doOnEach(integerSignal -> { // each封裝的詳細
					System.out.println("doOnEach.." + integerSignal);
				})// 1,2,3,4,5,6,7,0
				.doOnError(throwable -> {
					System.out.println("資料庫已經儲存了例外：" + throwable.getMessage());
				})
				.doOnError(throwable -> {
					System.out.println("資料庫已經儲存了例外q：" + throwable.getMessage());
				})
				.map(integer -> 100 / integer)
				.doOnNext(integer -> System.out.println("元素到哈：" + integer))

				.subscribe(System.out::println);
	}

	// Mono<Integer>： 只有一個Integer
	// Flux<Integer>： 有很多Integer
	public void fluxDoOn(String[] args) throws IOException, InterruptedException {
		// Mono<Integer> just = Mono.just(1);
		//
		// just.subscribe(System.out::println);

		// 空流: 鍊式API中，下面的操作符，操作的是上面的流。
		// 事件感知API：當流發生什麼事的時候，觸發一個回調,系統呼叫提前定義好的鉤子函數（Hook【鉤子函數】）；doOnXxx；
		Flux<Integer> flux = 
				Flux.range(1, 7)
				.delayElements(Duration.ofSeconds(1)).doOnComplete(() -> {
			System.out.println("流正常結束...");
		}).doOnCancel(() -> {
			System.out.println("流已取消...");
		}).doOnError(throwable -> {
			System.out.println("流出錯誤..." + throwable);
		}).doOnNext(integer -> {
			System.out.println("doOnNext..." + integer);
		}); // 有一個訊號：此時代表完成訊號

		flux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				System.out.println("訂閱者與發布者綁定好了：" + subscription);
				request(1); // 背壓
			}

			@Override
			protected void hookOnNext(Integer value) {
				System.out.println("元素到達：" + value);
				if (value < 5) {
					request(1);
					if (value == 3) {
						int i = 10 / 0;
					}
				} else {
					cancel();// 取消訂閱
				}
				; // 繼續要元素
			}

			@Override
			protected void hookOnComplete() {
				System.out.println("資料流結束");
			}

			@Override
			protected void hookOnError(Throwable throwable) {
				System.out.println("資料流異常");
			}

			@Override
			protected void hookOnCancel() {
				System.out.println("資料流被取消");
			}

			@Override
			protected void hookFinally(SignalType type) {
				System.out.println("final 結束訊號：" + type);
				// 正常、異常
				// try {
				// //業務
				// }catch (Exception e){
				//
				// }finally {
				// //結束
				// }
			}
		});

		Thread.sleep(2000);

		// Flux<Integer> range = Flux.range(1, 7);

		System.in.read();
	}

	// 測試Mono
	public void Mono() {
		// Mono: 0|1個元素的流
		// Flux: N個元素的流； N>1
		// 發布者發布資料流：源頭

		// 1、單元素的流
		Mono<Integer> just = Mono.just(1);

		// 流不消費就沒用；消費就是訂閱
		just.subscribe(e -> System.out.println("e = " + e));
		// 3、empty 空的流
		Mono<Object> empty = Mono.empty();

		empty.subscribe(e -> System.out.println("e = " + e), // onNext
				e -> System.out.println("錯誤了"), // onError
				() -> System.out.println("完成了") // onComplete
		);

	}

	// 測試Flux
	public void flux() throws IOException {
		// Mono: 0|1個元素的流
		// Flux: N個元素的流； N>1
		// 發布者發布資料流：源頭

		// 1、多元素的流
		Flux<Integer> just = Flux.just(1, 2, 3, 4, 5); //

		// 流不消費就沒用；消費就是訂閱
		just.subscribe(e -> System.out.println("e1 = " + e));
		// 一個資料流可以有很多消費者
		just.subscribe(e -> System.out.println("e2 = " + e));

		// 對每個消費者來說流都是一樣的； 其實就是一種廣播模式；

		System.out.println("==========");
		// 2、interval 定時，就是每隔多久產生一個元素
		Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));// 每秒產生一個從0開始的遞增數字

		flux.subscribe(System.out::println);

		// 3、empty 空的流
		Flux<Object> empty = Flux.empty(); // 只有一個信號，此時代表完成信號

		empty.subscribe(e -> System.out.println("e = " + e), // onNext
				e -> System.out.println("錯誤了"), // onError
				() -> System.out.println("完成了") // onComplete
		);

		System.in.read();
	}
}
