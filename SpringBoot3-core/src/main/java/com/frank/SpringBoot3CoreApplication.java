package com.frank;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.frank.listener.MyApplicationListener;

@SpringBootApplication
public class SpringBoot3CoreApplication {

	public static void main(String[] args) {
		
		SpringApplication application = new SpringApplication(SpringBoot3CoreApplication.class);
		application.addListeners(new MyApplicationListener());
		
		application.run(args);
		
//		SpringApplication.run(SpringBoot3CoreApplication.class, args);
	}

	
	
	 @Bean
	  public ApplicationRunner applicationRunner() {
		return args -> {
			System.out.println("ApplicationRunner 執行中...");
		}; 
		  
	  }
     @Bean
     public CommandLineRunner commandLineRunner() {
   	  return args -> {
   		  System.out.println("CommandLineRunner 執行中...");
   	  }; 
   	  
     }
}
