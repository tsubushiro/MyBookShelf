package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Owner;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BookShelfRepository;
import com.example.demo.repository.CommonBookRepository;
import com.example.demo.repository.MemoCommentRepository;
import com.example.demo.repository.ReadPlanRepository;
import com.example.demo.repository.ReadRecordRepository;

@Service
public class OwnerService {
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	BookShelfRepository bookShelfRepository;
	
	@Autowired
	CommonBookRepository commonBookRepository;
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	ReadRecordRepository readRecordRepository;
	
	@Autowired
	MemoCommentRepository memoCommentRepository;
	
	// 所有者チェック
	// userIdと比較して所有者だったらtrueを戻す
	public boolean isOwnerBookShelf(int bookId,int userId) {
		return (bookShelfRepository.getOwnerById(bookId) == userId);
	}
	public boolean isOwnerReadPlan(int readPlanId,int userId) {
		return (readPlanRepository.getOwnerById(readPlanId) == userId);
	}
	public boolean isOwnerReadRecord(int readRecordId,int userId) {
		return (readRecordRepository.getOwnerById(readRecordId) == userId);
	}
	public boolean isOwerMemoComment(int memoCommentId,int userId) {
		return (memoCommentRepository.getOwnerById(memoCommentId) == userId);
	}
	
	// 所有者のIDを戻す
	public int getBookShelfOwnerById(int bookId) {
		return bookShelfRepository.getOwnerById(bookId);
	}
	public int getReadPlanOwnerById(int readPlanId) {
		return readPlanRepository.getOwnerById(readPlanId);
	}
	public int getReadRecordOwnerById(int readRecordId) {
		return readRecordRepository.getOwnerById(readRecordId);
	}
	public int getMemoCommentOwnerById(int memoCommentId) {
		return memoCommentRepository.getOwnerById(memoCommentId);
	}
	
	// Ownerオブジェクト生成
	public Owner getOwnerByUserId(int userId) {
		return new Owner(userId,accountRepository.getUserName(userId));
	}
}
