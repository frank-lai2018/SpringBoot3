package com.frank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
	
	/**
	 * 允許Object類型的key-value，都可以轉為json進行儲存。 
	* @param redisConnectionFactory 自動配置了連接工廠
	 * @return
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		
		//把物件轉成json的格式進行儲存
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
