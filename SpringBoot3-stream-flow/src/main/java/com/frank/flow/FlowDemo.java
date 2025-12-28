package com.frank.flow;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class FlowDemo {
	// 定義流中間操作處理器； 只用寫訂閱者的介面
	static class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {

		private Flow.Subscription subscription; // 儲存綁定關係

		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			System.out.println("processor訂閱綁定完成");
			this.subscription = subscription;
			subscription.request(1); // 找上游要一個數據
		}

		@Override // 資料到達，觸發這個回調
		public void onNext(String item) {
			System.out.println("processor拿到資料：" + item);
			// 再加工
			item += "：哈哈";
			submit(item);// 把我加工後的資料發出去
			subscription.request(1); // 再要新數據
		}

		@Override
		public void onError(Throwable throwable) {

		}

		@Override
		public void onComplete() {

		}
	}

	/**
	 * 1、Publisher：發布者 2、Subscriber：訂閱者 3、Subscription： 訂閱關係 4、Processor： 處理器
	 * 
	 * @param args
	 */

	// 發布訂閱模型：觀察者模式，
	public static void main(String[] args) throws InterruptedException {

		// 1、定義一個發布者；發布資料；
		SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

		// 2、定一個中間運算： 給每個元素加個 哈哈 前綴
		MyProcessor myProcessor1 = new MyProcessor();
		MyProcessor myProcessor2 = new MyProcessor();
		MyProcessor myProcessor3 = new MyProcessor();

		// 3、定義一個訂閱者；訂閱者感興趣發布者的資料；
		Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {

			private Flow.Subscription subscription;

			@Override // 在訂閱時 onXxxx：在xxx事件發生時，執行這個回呼
			public void onSubscribe(Flow.Subscription subscription) {
				System.out.println(Thread.currentThread() + "訂閱開始了：" + subscription);
				this.subscription = subscription;
				// 從上游請求一個數據
				subscription.request(1);
			}

			@Override // 在下一個元素到達時； 執行這個回呼； 接受到新數據
			public void onNext(String item) {
				System.out.println(Thread.currentThread() + "訂閱者，接受到資料：" + item);

				if (item.equals("p-7")) {
					subscription.cancel(); // 取消訂閱
				} else {
					subscription.request(1);
				}
			}

			@Override // 在錯誤發生時，
			public void onError(Throwable throwable) {
				System.out.println(Thread.currentThread() + "訂閱者，接受到錯誤訊號：" + throwable);
			}

			@Override // 在完成時
			public void onComplete() {
				System.out.println(Thread.currentThread() + "訂閱者，接受到完成訊號：");
			}
		};

		// 4、綁定發布者和訂閱者
		publisher.subscribe(myProcessor1); // 此時處理器相當於訂閱者
		myProcessor1.subscribe(myProcessor2); // 此時處理器相當於發行者
		myProcessor2.subscribe(myProcessor3);
		myProcessor3.subscribe(subscriber); // 鍊錶關係綁定出責任鏈。
		// 綁定操作；就是發布者，記住了所有訂閱者都有誰，有資料後，給所有訂閱者把資料推送過去。

		// publisher.subscribe(subscriber);

		for (int i = 0; i < 10; i++) {
			// 發布10條數據
			if (i == 5) {
				// publisher.closeExceptionally(new RuntimeException("5555"));
			} else {
				publisher.submit("p-" + i);
			}
			// publisher發布的所有資料在它的buffer區；
			// 中斷
			// publisher.closeExceptionally();
		}

		// ReactiveStream
		// jvm底層對於整個發布訂閱關係做好了 非同步+快取區處理 = 響應式系統；

		// 發布者通道關閉
		publisher.close();

		// publisher.subscribe(subscriber2);

		// 發布者有數據，訂閱者就會拿到

		Thread.sleep(20000);

	}
}
