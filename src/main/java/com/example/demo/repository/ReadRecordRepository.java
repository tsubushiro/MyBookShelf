package com.example.demo.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ReadRecord;

// 読書履歴
@Repository
public class ReadRecordRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	// 読書履歴の登録
	public boolean insert(ReadRecord readRecord) {
		String sql="INSERT INTO readRecord(readPlanId,recordDate,readPage) values(?,?,?)";
		int result = jdbcTemplate.update(sql,
				readRecord.getReadPlanId(), // 読書プランID
				Date.valueOf(readRecord.getRecordDate()), // 記録日付
				readRecord.getReadPage() // 記録ページ数
				);
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
	// 同じプランIDで同日の読書登録があるのかを調べる
	// あったらtrue
	public boolean findOverLap(ReadRecord readRecord) {
		String sql="SELECT * from readRecord where readPlanId = ? and recordDate = ? and status=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,
				readRecord.getReadPlanId(), // 読書プランID
				Date.valueOf(readRecord.getRecordDate()) // 記録日付
				);
		if( resultList.size()==0 ) return false;
		return true;
	}
	
	// 同じプランIDで同日の登録がある場合、上書きする
	// Chart.jsでX軸の重複があるとグラフが表示されないので
	public boolean update(ReadRecord readRecord) {
		String sql="UPDATE readRecord set readPlanId=?,recordDate=?,readPage=? "
				+ "where readPlanId=? and recordDate=? and status=0";
		// UPDATE readRecord set readPlanId=5,recordDate='2023-12-26',readPage=999 where readPlanId=5 and recordDate='2023-12-26';
		int result = jdbcTemplate.update(sql,
				readRecord.getReadPlanId(), // 読書プランID
				Date.valueOf(readRecord.getRecordDate()), // 記録日付
				readRecord.getReadPage(), // 記録ページ数
				// where 以降
				readRecord.getReadPlanId(), // 読書プランID 
				Date.valueOf(readRecord.getRecordDate()) // 記録日付
				);
		if(result == 0) {
			return false;//登録失敗
		}
		return true;
	}
	
	// 読書履歴のリストの取得
	public List<ReadRecord> findByReadRecordId(int readPlanId) {
		String sql="SELECT * FROM readRecord WHERE readPlanId = ? AND status=0 ORDER BY recordDate desc , readPage desc";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,readPlanId);
		// System.out.println(resultList);
		List<ReadRecord> readRecordList = new ArrayList<>();
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int readRecordId = (int) result.get("readRecordId");
				// int readPlanId = (int) result.get("readPlanId");
				LocalDate recordDate = ((Date) result.get("recordDate")).toLocalDate();
				int readPage = (int) result.get("readPage");
				ReadRecord readRecord = new ReadRecord(
						readRecordId,
						readPlanId,
						recordDate,
						readPage
				);
				readRecordList.add(readRecord);
			}
		}
		return readRecordList;
	}
	
	// 所有者のIDを取得
	public int getOwnerById(int readRecordId) {
		String sql = "select userid from bookShelf"
				+ " where bookid in (select bookId from readPlan"
				+ " where readPlanId in (select readPlanId from readRecord"
				+ " where readRecordId = ?))";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,readRecordId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				return (int) result.get("userId");
			}
		}
		return -1;
	}
	
	// ユーザーに紐づいた読書履歴の削除
	public boolean removeByUserId(int userId) {
		String sql="UPDATE readRecord SET status=-1"
				+ " where readPlanId in (select readPlanId from readPlan"
				+ " where bookId in (select bookId from bookShelf"
				+ " where userId=?))";
		int result = jdbcTemplate.update(sql,userId);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
	
	// 読書履歴の削除
	public boolean removeById(int readRecordId) {
		String sql="UPDATE readRecord SET status=-1"
				+ " where readRecordId=? ";
		// System.out.println(sql);
		int result = jdbcTemplate.update(sql,readRecordId);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
}
