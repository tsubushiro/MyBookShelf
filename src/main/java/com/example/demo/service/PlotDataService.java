package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PlotData;
import com.example.demo.entity.ReadPlan;
import com.example.demo.entity.ReadPlanView;
import com.example.demo.entity.ReadRecord;
import com.example.demo.repository.ReadPlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Service
public class PlotDataService {
	
	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	ReadPlanService readPlanService;
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	ReadPlanViewService readPlanViewService;
	
	// 読書プランからグラフプロット用のデータを取得
	// [プランのプロットデータのリスト、履歴のプロットデータのリスト]
	public List<List<PlotData>> getPlotData(int readPlanId){
		ReadPlanView readPlanView = readPlanViewService.findById(readPlanId);
		LocalDateTime startPlanDate = readPlanView.getStartPlanDate().atStartOfDay()/*.minusHours(12)*/;
		LocalDateTime endPlanDate = readPlanView.getEndPlanDate().atStartOfDay()/*.plusHours(12)*/;
		int pageCount = readPlanView.getCommonBook().getPageCount();
		// プロットデータの取得
		// 計画
		List<PlotData> planDataList = new ArrayList<>();
		planDataList.add(new PlotData(startPlanDate,0));
		planDataList.add(new PlotData(endPlanDate,pageCount));
		// 実際
		List<PlotData> recordDataList = new ArrayList<>();
		List<ReadRecord> readRecordList = readPlanView.getReadRecordList();
		for(ReadRecord readRecord : readRecordList) {
			LocalDateTime x = readRecord.getRecordDate().atStartOfDay();
			int y = readRecord.getReadPage();
			recordDataList.add(new PlotData(x,y));
		}
		List<List<PlotData>> bothList = Arrays.asList(planDataList,recordDataList);		
		return bothList;
	}
	
	// 読書プランIDから座標値のJSON文字列を返す
	// グラフの表示のためプランの日付は±0.5日ほどずらす
	public String getPlotDataJSON(int readPlanId) {
		String plotDataJson = new String();
		ObjectMapper objectMapper = new ObjectMapper();
		// https://qiita.com/Hyuga-Tsukui/items/3cd1268469baf253dadb
		// Jackson で LocalDateを使う方法
		JavaTimeModule jtm = new JavaTimeModule();
		// 時間のシリアライズ処理
//		jtm.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		objectMapper.registerModule(jtm);
		
		try {
			// グラフの整形のため、開始日、終了日それぞれに±0.5日している
			List<List<PlotData>> bothList = getPlotData(readPlanId);
			PlotData startPlan = bothList.get(0).get(0);
			PlotData endPlan = bothList.get(0).get(1);
			LocalDateTime startPlanDate = startPlan.getX().minusHours(12);
			LocalDateTime endPlanDate = endPlan.getX().plusHours(12);
			startPlan.setX(startPlanDate);
			endPlan.setX(endPlanDate);
			// JSONへの変換
			plotDataJson = objectMapper.writeValueAsString(bothList);
//		}catch (JsonProcessingException e) {
		}catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// System.out.println(bothList);
		return plotDataJson;
	}
	
	// 所有本に関わる進捗度を取得
	// finish=0状態のreadPlanを取得して進捗度を数値で返す
	// ない場合は-1を返す
	public int getProgress(int bookId) {
		int result = -1;
		List<ReadPlan> readPlanList = readPlanRepository.findByBookId(bookId);
//		System.out.println(readPlanList);
		if(readPlanList.size() == 0) return -1; // 読書プランがなくて取得できない
		int readPlanId = -1;
		for(ReadPlan readPlan:readPlanList) {
			if(readPlan.getFinished()==0) {
				readPlanId = readPlan.getReadPlanId();
				break;
			}
		}
//		System.out.println(readPlanId);
		if(readPlanId == -1) return -1; // finished=0の読書プランが取得できない
		List<List<PlotData>> plotDataList = getPlotData(readPlanId);
		if(plotDataList.size() <=1 ) return -1;// 読書履歴取得できない
//		System.out.println(plotDataList);
		List<PlotData> planPlotDataList = plotDataList.get(0);
		List<PlotData> recordPlotDataList = plotDataList.get(1);
		if(planPlotDataList.size() < 1 ) return -1; // 読書プランない
		if(recordPlotDataList.size() < 1 ) return 0; // プランはあるが読書履歴ない(0%)
		// 各値を取得
		int planPage = planPlotDataList.stream().max((a,b)->(a.getY()-b.getY())).get().getY();
		int recordPage = recordPlotDataList.stream().max((a,b)->(a.getY()-b.getY())).get().getY();
//		System.out.println(planPage+","+recordPage);
		// 百分率
		result = (int)Math.floor(((double) recordPage)/((double) planPage) * 100.0);
		return result;
	}
}
