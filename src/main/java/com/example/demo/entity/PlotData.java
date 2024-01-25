package com.example.demo.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class PlotData {
	@DateTimeFormat(pattern = "yyyy-MM-dd")   // 入力時の期待フォーマット
	private LocalDateTime x;
	private int y;
}
