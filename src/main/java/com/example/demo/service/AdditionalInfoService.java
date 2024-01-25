package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AdditionalInfo;
import com.example.demo.entity.MemoComment;
import com.example.demo.entity.ReadPlan;
import com.example.demo.repository.MemoCommentRepository;
import com.example.demo.repository.ReadPlanRepository;

@Service
public class AdditionalInfoService {
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	MemoCommentRepository memoCommentRepository;

	// bookIdから付帯情報を取得
	public AdditionalInfo findById(int bookId) {
		AdditionalInfo additionalInfo ; 
		List<ReadPlan> readPlanList = readPlanRepository.findByBookId(bookId);//読書プランの情報
		List<MemoComment> memoCommentList = memoCommentRepository.findByBookId(bookId) ; // あとで実装
		additionalInfo = new AdditionalInfo(readPlanList,memoCommentList); 
		return additionalInfo;
	}
}
