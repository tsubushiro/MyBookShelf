package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MyBookShelfApplication  extends SpringBootServletInitializer implements WebMvcConfigurer {
	
//	@Autowired
//	Test test;
	
	public static void main(String[] args) {
		SpringApplication.run(MyBookShelfApplication.class, args)
//		.getBean(MyBookShelfApplication.class)
//		.execute()
		;
	}
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyBookShelfApplication.class);
    }

//	private void execute() {
//		test.execute();
//	}
}
