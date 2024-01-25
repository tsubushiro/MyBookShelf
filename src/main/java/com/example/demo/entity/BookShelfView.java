package com.example.demo.entity;

import java.util.List;

import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 所有本全ユーザ表示
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class BookShelfView {
	private Account account;
	private List<BookView> bookViewList;
	private int size;
}
