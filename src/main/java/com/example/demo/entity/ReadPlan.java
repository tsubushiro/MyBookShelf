package com.example.demo.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 読書プラン
@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class ReadPlan {
	@Id
	private int readPlanId;// 読書プランID
	private int bookId;//所有本ID
	@DateTimeFormat(pattern = "yyyy-MM-dd") // これをいれることでフォーマットが自動変換される
	private LocalDate startPlanDate;// プランの開始日
	@DateTimeFormat(pattern = "yyyy-MM-dd") // これをいれることでフォーマットが自動変換される
	private LocalDate endPlanDate;// プランの終了日
	private LocalDate startRecordDate;// 実際の開始日
	private LocalDate endRecordDate;// 実際の終了日
	private int finished;// 読了
//	private List<ReadRecord> readRecordList;// 読書履歴
}
