package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.test.Test;

@SpringBootApplication
public class MyBookShelfApplication {
	
	@Autowired
	Test test;
	
	public static void main(String[] args) {
		SpringApplication.run(MyBookShelfApplication.class, args)
//		.getBean(MyBookShelfApplication.class)
//		.execute()
		;
	}

	private void execute() {
		test.execute();
	}
}
