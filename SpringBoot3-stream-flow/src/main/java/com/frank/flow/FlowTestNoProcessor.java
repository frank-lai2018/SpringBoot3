package com.frank.flow;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;

public class FlowTestNoProcessor {
	
	public static void main(String[] args) throws InterruptedException {
		SubmissionPublisher<String> publisher = new SubmissionPublisher<String>();
		
		
		
		Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {
			
			private Subscription subscription;

			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println(Thread.currentThread()+"  訂閱開始........subscription="+subscription);
				this.subscription = subscription;
				subscription.request(1);
				
			}

			@Override
			public void onNext(String item) {
				System.out.println(Thread.currentThread()+" 訂閱者接收到數據 item = "+item);
				
				subscription.request(1);
				
			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println(Thread.currentThread()+" 發生異常了 throwable = "+throwable);
				
			}

			@Override
			public void onComplete() {
				System.out.println(Thread.currentThread()+" 訂閱者接收了所有數據.... ");
				
			}
		};
		
		Flow.Subscriber<String> subscriber2 = new Flow.Subscriber<String>() {
			
			private Subscription subscription;
			
			@Override
			public void onSubscribe(Subscription subscription) {
				System.out.println(Thread.currentThread()+" subscriber2 訂閱開始........subscription="+subscription);
				this.subscription = subscription;
				subscription.request(1);
				
			}
			
			@Override
			public void onNext(String item) {
				System.out.println(Thread.currentThread()+" subscriber2 訂閱者接收到數據 item = "+item);
				
				subscription.request(1);
				
			}
			
			@Override
			public void onError(Throwable throwable) {
				System.out.println(Thread.currentThread()+" subscriber2 發生異常了 throwable = "+throwable);
				
			}
			
			@Override
			public void onComplete() {
				System.out.println(Thread.currentThread()+" subscriber2 訂閱者接收了所有數據.... ");
				
			}
		};
		
		publisher.subscribe(subscriber);
		publisher.subscribe(subscriber2);
		
		Thread.sleep(1000);
		
		for(int i = 0;i<10;i++) {
			
			publisher.submit("提供數據:"+i);
		}
		
		Thread.sleep(20000);
	}

}
