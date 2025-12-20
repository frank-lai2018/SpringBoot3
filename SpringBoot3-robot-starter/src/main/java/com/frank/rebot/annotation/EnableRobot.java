package com.frank.rebot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import java.lang.annotation.ElementType;
import com.frank.rebot.config.RobotAutoConfiguration;


/**
 * 自訂義starter 導入Bean的兩個方式(因為包名一定不一樣，所以要自己放入IOC容器中)
 * 1.使用@EnableRobot 引入RobotAutoConfiguration，使用者不只要導入starter依賴，還需要在SpringBoot主類上加上@EnableRobot註解
 * 2.使用META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 這個只要在這個文件上放入RobotAutoConfiguration全類名即可，
 *   使用者只需導入這個starter依賴即可自動配置
 * */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(RobotAutoConfiguration.class)
public @interface EnableRobot {

}
