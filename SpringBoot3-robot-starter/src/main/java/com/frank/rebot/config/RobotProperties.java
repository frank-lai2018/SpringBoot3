package com.frank.rebot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "robot")
@Data
@Component
public class RobotProperties {

	private String name;
	private String age;
	private String email;
}
