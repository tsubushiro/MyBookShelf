package com.example.demo.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.repository.MemoCommentRepository;

@Component
public class TestImpl implements Test {

	// 共通本登録
	@Autowired
	MemoCommentRepository repo;
	
	
	@Override
	public void execute() {
//		MemoComment memoComment = new MemoComment(1,0,"タイトル","あはははは",null);
//		repo.insert(memoComment);
//		repo.update(memoComment);
		System.out.println(repo.findById(3));
	}
}
