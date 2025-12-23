package com.frank.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

@Component
public class MyHahaTopicListener {
	// 預設的監聽是從訊息佇列最後一個訊息開始拿。新消息才能拿到
	@KafkaListener(topics = {"news","frank-topic1"}, groupId = "haha")
	public void haha​​(ConsumerRecord record) {
		// 1、獲取訊息的各種詳細信息
		// String topic = record.topic();
		Object key = record.key();
		Object value = record.value();
		System.out.println("收到訊息：key【" + key + "】 value【" + value + "】");
	}

	// 拿到以前的完整訊息;
	 @KafkaListener(groupId = "hehe",topicPartitions={
	            @TopicPartition(topic="news",partitionOffsets={
	                    @PartitionOffset(partition="0",initialOffset = "0")
	            })
	    })
	public void hehe(ConsumerRecord record) {
		Object key = record.key();
		Object value = record.value();
		System.out.println("======收到訊息：key【" + key + "】 value【" + value + "】");
	}
}
