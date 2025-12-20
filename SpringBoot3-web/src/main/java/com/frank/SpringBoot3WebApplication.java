package com.frank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.frank.rebot.annotation.EnableRobot;

@SpringBootApplication
//@Import(com.frank.rebot.config.RobotAutoConfiguration.class)
@EnableRobot
public class SpringBoot3WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot3WebApplication.class, args);
	}

}
