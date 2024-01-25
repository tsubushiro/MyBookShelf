package com.example.demo.entity;

import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// セッションスコープは今回いらない
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class LoginUser {
	private String name;
	private String pass;
}
