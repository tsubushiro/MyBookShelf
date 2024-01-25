package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ReadRecord;
import com.example.demo.repository.ReadRecordRepository;

@Service
public class ReadRecordService {

	@Autowired
	ReadRecordRepository readRecordRepository;
	
	public boolean insert(ReadRecord readRecord) {
		if(readRecordRepository.findOverLap(readRecord)) { // 重複がある場合
			return readRecordRepository.update(readRecord);//上書き
		}else { // ない場合は挿入
			return readRecordRepository.insert(readRecord);
		}
	}
	
	public boolean removeById(int readRecord) {
		// System.out.println("よんだ？");
		return readRecordRepository.removeById(readRecord);
	}
}
