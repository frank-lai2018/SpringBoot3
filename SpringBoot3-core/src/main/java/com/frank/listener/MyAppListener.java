package com.frank.listener;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

/**
 * @author lfy
 * @Description SpringBoot應用生命週期監聽
 * @create 2023-04-24 14:46
 */

/**
 * Listener 先要從 META-INF/spring.factories 讀到
 *
 * 1、引導： 利用 BootstrapContext 引導整個專案啟動 starting：
 * 應用程式開始，SpringApplication的run方法一調用，只要有了 BootstrapContext 就執行
 * environmentPrepared： 環境準備好（把啟動參數等綁定到環境變數中），但是ioc還沒創建；【調一次】 2、啟動：
 * contextPrepared： ioc容器建立並準備好，但是sources（主配置類別，跟一些自己寫的配置，例如:@Configuration ）沒載入。並關閉引導上下文；元件都沒建立 【調一次】
 * contextLoaded： ioc容器載入。主配置類別載入進去了。但是ioc容器還沒刷新（我們的bean沒創建）。
 * =======截止以前，ioc容器裡面還沒製造bean呢======= started： ioc容器刷新了（所有bean造好了），但是 runner
 * 沒呼叫。 ready: ioc容器刷新了（所有bean造好了），所有runner調用完了。 3、運行 以前步驟都正確執行，代表容器running。
 */
public class MyAppListener implements SpringApplicationRunListener {
	@Override
	public void starting(ConfigurableBootstrapContext bootstrapContext) {

		System.out.println("=====starting=====正在啟動======");
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
			ConfigurableEnvironment environment) {
		System.out.println("=====environmentPrepared=====環境準備完成======");
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		System.out.println("=====contextPrepared=====ioc容器準備完成======");
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		System.out.println("=====contextLoaded=====ioc容器載入完成======");
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		System.out.println("=====started=====啟動完成======");
	}

	@Override
	public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
		System.out.println("=====ready=====準備就緒======");
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		System.out.println("=====failed=====應用程式啟動失敗======");
	}
}