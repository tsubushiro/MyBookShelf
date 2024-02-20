package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.Account;
import com.example.demo.entity.LoginUser;
import com.example.demo.service.UserService;

@SpringBootTest
class MyBookShelfApplicationTests {

	
	@Autowired
	UserService userservice;
	
//	@Test
	void contextLoads() {
		System.out.println("start");
//		userservice.getUserName(1);
		Account account = new Account(
				0,
				"test2",
				"1234",
				15,
				"test@gmail.com"
				);
		System.out.println(userservice.createNewAccount(account));
	}
	
//	@Test
	void loginCheck() {
		LoginUser loginUser = new LoginUser(
				"test",
				"1234"
				);
		System.out.println(userservice.loginCheck(loginUser));
	}

//	@Test
	void registerCheck() {
		System.out.println("start");
		userservice.getUserName(1);
		Account account = new Account(
				0,
				"test",
				"1234",
				15,
				"test@gmail.com"
				);
		System.out.println(userservice.registerCheck(account));
	}
	
	@Test
	void removeAccount() {
		System.out.println("start");
		Account account = new Account(
				2,
				"test2",
				"1234",
				15,
				"test@gmail.com"
				);
		System.out.println(userservice.removeAccount(account));
	}
//	@Test
	void getUserName() {
		System.out.println("start");
		System.out.println(userservice.getUserName(18));
	}
//	@Test
	void updateAccount() {
		System.out.println("start");
		Account account = new Account(
				18,
				"test",
				"1234",
				17,
				"testTest@gmail.com"
				);
		System.out.println(userservice.updateAccount(account));
	}
//	@Test
	void encodeTest() {
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
	    System.out.println(bcryptPasswordEncoder.encode("1234"));
	    System.out.println(bcryptPasswordEncoder.encode("1234"));
	    System.out.println(bcryptPasswordEncoder.encode("1234"));
	}
}
