package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ReadPlan;
import com.example.demo.entity.ReadRecord;
import com.example.demo.repository.ReadPlanRepository;
import com.example.demo.repository.ReadRecordRepository;

@Service
public class ReadPlanService {

	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	ReadRecordRepository readRecordRepository;
	
	// 読書プランの登録
	public boolean insert(ReadPlan readPlan) {
		return readPlanRepository.insert(readPlan);
	}
	
	// 読書プランの検索
	public ReadPlan findByReadPlanId(int readPlanId) {
		return readPlanRepository.findByReadPlanId(readPlanId);
	}
	
	// 読書プランの終了
	public boolean finished(ReadPlan readPlan) {
		List<ReadRecord> readRecordList = readRecordRepository.findByReadRecordId(readPlan.getReadPlanId());
		LocalDate startRecordDate = LocalDate.MAX;
		LocalDate endRecordDate = LocalDate.MIN;
		// これまでの記録
		for(ReadRecord readRecord:readRecordList) {
			LocalDate readDate = readRecord.getRecordDate();
			if(readDate.isBefore(startRecordDate)) {
				startRecordDate = readDate;
			}
			if(readDate.isAfter(endRecordDate)) {
				endRecordDate = readDate;
			}
		}
		readPlan.setStartRecordDate(startRecordDate);
		readPlan.setEndRecordDate(endRecordDate);
//		System.out.println(readPlan);
		return readPlanRepository.finished(readPlan);
	}
	
	// 読書中の読書プランの終了有無をチェックして
	// 0があったら終わってないのでfalse→読書プランのリンクを表示しない
	// それ以外は表示する
	public boolean checkNewReadPlan(int bookId) {
		// リストのサイズがゼロ　つくる
		// 0がある　つくらない
		// 全部1 つくる	
		List<ReadPlan> readPlanList = readPlanRepository.findByBookId(bookId);
		if(readPlanList.size() == 0) return true;
		for(ReadPlan readPlan:readPlanList) {
			// System.out.println(readPlan);
			if(readPlan.getFinished()==0) return false;
		}
		return true;
	}
}
