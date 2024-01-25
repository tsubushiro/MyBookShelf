package com.example.demo.entity;

import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 閲覧している物の所有権に関わる情報
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class Owner {
	private int userId;
	private String name;
}
