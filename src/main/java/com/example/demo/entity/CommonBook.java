package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonBook {
	private String commonBookId;	// ISBN
	private String title;	// タイトル
	private String authors;	// 著者(一人のみ)
	private String publisher;	// 出版社
	private int pageCount;	// ページ数
	private String thumbnail;	// サムネイル
}
