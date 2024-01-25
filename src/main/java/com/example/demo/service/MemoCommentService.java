package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MemoComment;
import com.example.demo.repository.MemoCommentRepository;

@Service
public class MemoCommentService {

	@Autowired
	MemoCommentRepository memoCommentRepository;
	
	// メモの検索
	public MemoComment findById(int memoCommentId) {
		return memoCommentRepository.findById(memoCommentId);
	}
	// メモの登録
	public boolean insert(MemoComment memoComment) {
		return memoCommentRepository.insert(memoComment);
	}
	// メモの更新
	public boolean update(MemoComment memoComment) {
		return memoCommentRepository.update(memoComment);
	}
	// メモのリストの取得
	public List<MemoComment> findByBookId(int bookId){
		return memoCommentRepository.findByBookId(bookId);
	}
	// 所有者のIDを取得
	public int getOwnerById(int memoCommentId) {
		return memoCommentRepository.getOwnerById(memoCommentId);
	}
	// ユーザーに紐づいたメモの削除
	public boolean removeByUserId(int userId) {
		return memoCommentRepository.removeByUserId(userId);
	}
	// メモの削除
	public boolean removeById(int memoComment) {
		return memoCommentRepository.removeById(memoComment);
	}
	// ユーザ所有の最新のメモID
	public int findLastIdByBookId(int bookId) {
		return memoCommentRepository.findLastIdByBookId(bookId);
	}
}
