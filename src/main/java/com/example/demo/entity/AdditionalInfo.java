package com.example.demo.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfo{
	private List<ReadPlan> readPlanList; // 複数にしておくがユーザがアクセスできるのは現行の読書プラン
	private List<MemoComment> memoCommentList;// メモ書きのリスト
}