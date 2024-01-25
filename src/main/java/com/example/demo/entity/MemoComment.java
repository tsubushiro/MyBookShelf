package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoComment{
	@Id
	private int memoCommentId; //コメントID
	private int bookId; //所有本ID
	@Length(min=1,message="タイトルは1文字以上で入力してください")
	@Length(max=30,message="タイトルは30文字以内で入力してください")
	private String title; // タイトル
	@Length(min=1,message="メモは1文字以上で入力してください")
	@Length(max=300,message="メモは300文字以内で入力してください")
	private String text; // テキスト
	LocalDateTime recordDate;//登録日付
}