package com.frank.rebot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.frank.rebot.controller.HelloControler;
import com.frank.rebot.service.HelloService;

@Configuration
@Import({HelloControler.class,HelloService.class,RobotProperties.class})
public class RobotAutoConfiguration {

}
