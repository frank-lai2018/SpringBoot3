package com.frank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;



/**
 * KafkaAutoConfiguration提供以下功能
 * 1、KafkaProperties：kafka的所有配置; 以 spring.kafka開始
 * - bootstrapServers: kafka叢集的所有伺服器位址
 * - properties: 參數設定
 * - consumer: 消費者
 * - producer: 生產者
 * ...
 * 2、@EnableKafka: 開啟Kafka的註解驅動功能
 * 3、KafkaTemplate: 收發訊息
 * 4、KafkaAdmin： 維護主題等...
 * 5、@EnableKafka + @KafkaListener 接受訊息
 * 1）、消費者來接受訊息，需有group-id
 * 2）、收訊息使用 @KafkaListener + ConsumerRecord
 * 3）、spring.kafka 開始的所有配置
 * 6、核心概念
 * 分區： 分散存儲，1T的資料分散到N個節點
 * 副本： 備份機制，每個小分割區的資料都有備份
 * 主題： topics； 訊息是傳送給某個主題
 */

@EnableKafka
@SpringBootApplication
public class SpringBoot3KafkaMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot3KafkaMessageApplication.class, args);
	}

}
