package com.example.demo.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {
	@Id
	private int memoId; //メモID
	private int bookId; //所有本ID
}
