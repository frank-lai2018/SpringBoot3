package com.frank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.frank.bean.Person;

@SpringBootApplication
public class SpringBoot3YmalApplication {

	public static void main(String[] args) {
		var ioc = SpringApplication.run(SpringBoot3YmalApplication.class, args);

		Person person = ioc.getBean(Person.class);
		System.out.println("person：" + person);
		System.out.println("==== 用|表示大文本，会保留格式");
		String s = person.getChild().getText().get(2);
		System.out.println(s);
		System.out.println("==== 用>表示大文本，会压缩换行变成 空格");
		var ss = person.getChild().getText().get(3);
		System.out.println(ss);

		System.out.println("==== 用|表示大文本，会压缩换行变成 空格");
		var ss2 = person.getChild().getText().get(4);
		System.out.println(ss2);
	}

}
