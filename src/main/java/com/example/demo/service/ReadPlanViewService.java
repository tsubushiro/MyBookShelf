package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.BookShelf;
import com.example.demo.entity.CommonBook;
import com.example.demo.entity.ReadPlan;
import com.example.demo.entity.ReadPlanView;
import com.example.demo.entity.ReadRecord;
import com.example.demo.repository.ReadPlanRepository;
import com.example.demo.repository.ReadRecordRepository;

@Service
public class ReadPlanViewService {

	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	ReadRecordRepository readRecordRepository;
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	BookShelfService bookShelfService;
	
	
	// 読書履歴の登録
	public boolean insert(ReadRecord readRecord) {
		return readRecordRepository.insert(readRecord);
	}
	
	
	// 読書プランから読書プラン表示用エンティティへ変換
	public ReadPlanView convertFromReadPlan(ReadPlan readPlan){
		int readPlanId = readPlan.getReadPlanId();// 読書プランID
		int bookId = readPlan.getBookId();//所有本ID
		LocalDate startPlanDate = readPlan.getStartPlanDate();// プランの開始日
		LocalDate endPlanDate = readPlan.getEndPlanDate();// プランの終了日
		LocalDate startRecordDate = readPlan.getEndRecordDate();// 実際の開始日
		LocalDate endRecordDate = readPlan.getEndRecordDate();// 実際の終了日
		int finished = readPlan.getFinished();// 読了
		List<ReadRecord> readRecordList = readRecordRepository.findByReadRecordId(readPlanId);// 読書履歴
		BookShelf bookShelf = bookShelfService.findByBookId(bookId);// 本棚
		CommonBook commonBook = commonBookService.findById(bookShelf.getCommonBookId());// 本棚から共通本情報を取得
		ReadPlanView readPlanView = new ReadPlanView(
				readPlanId,
				bookId,
				startPlanDate,
				endPlanDate,
				startRecordDate,
				endRecordDate,
				finished,
				readRecordList,
				commonBook
				);
		return readPlanView;
	}	
	
	// 読書プランIDから読書プラン表示用エンティティへ変換
	public ReadPlanView findById(int readPlanId){
		ReadPlan readPlan = readPlanRepository.findByReadPlanId(readPlanId);
		return convertFromReadPlan(readPlan);
	}	
	
}
