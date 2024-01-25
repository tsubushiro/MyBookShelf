package com.example.demo.entity;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class ReadRecord {
	@Id
	private int readRecordId; // 記録ID
	private int readPlanId; // 読書プランID 
	@DateTimeFormat(pattern = "yyyy-MM-dd")// これをいれることでフォーマットが自動変換される
	private LocalDate recordDate; // 記録日
	@Range(min=1,message="ページ数は1以上です")
	private int readPage; // 読んだページ
}
