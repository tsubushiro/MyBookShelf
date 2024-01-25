package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 所有本
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class BookShelf {
	@Id
	private int bookId;// 所有本ID
	
	private int userId;// ユーザID
	
	@NotBlank(message="ISBNコードは13桁の数字です。")
	@Pattern(regexp="97[0-9-]{11,}",message="97からはじまる13桁のISBNコードを入力してください")
	private String commonBookId;// 共有本
	
	private int rank;// ランク
	
	private String tag;// タグ
	
	private int privateBook;// 公開
//	private int status;// ステイタス
}
