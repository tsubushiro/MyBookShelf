package com.example.demo.entity;

import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 所有本表示用
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class BookView {
	private int bookId;	// 所有本ID
	private String commonBookId;	// 共通本ID
	private String title;	// タイトル
	private String authors;	// 著者(一人のみ)
	private String publisher;	// 出版社
	private int pageCount;	// ページ数
	private String thumbnail;	// サムネイル
	private AdditionalInfo additionalInfo; //付帯情報
	// private List<Integer> readPlanId; //読書プランID
}
