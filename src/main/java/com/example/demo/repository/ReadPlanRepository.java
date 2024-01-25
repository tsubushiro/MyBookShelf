package com.example.demo.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ReadPlan;

@Repository
public class ReadPlanRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ReadRecordRepository readRecordRepository;
	
	// 読書プランの登録
	public boolean insert(ReadPlan readPlan) {
//		String sql="INSERT INTO READPLAN(BOOKID,STARTPLANDATE,ENDPLANDATE) VALUES(?,?,?)";
//		int result = jdbcTemplate.update(sql,
//				readPlan.getBookId(),
//				Date.valueOf(readPlan.getStartPlanDate()),
//				Date.valueOf(readPlan.getEndPlanDate())
//				);
		// readPlan表では同じbookIdを持つ行が複数存在する可能性があるが、
		// その中でfinish=0(読書中)は1つのみ。
		// 例えば、bookId=1を挿入するとき、
		// bookId=1,finish=0の行がある場合、挿入できない。
		// bookId=1の行がない場合
		// あるいはbookId=1,finish=1の場合は挿入できる
		String sql="INSERT INTO readPlan(bookId,startPlanDate,endPlanDate)"
				+ " SELECT ?,?,? "
				+ " WHERE NOT EXISTS ( "
				+ " SELECT * FROM readPlan "
				+ " WHERE bookId = ? AND finished = 0"
				+ ")";
		/*
		INSERT INTO readPlan (bookId, startPlanDate, endPlanDate)
		SELECT 1, '2023-01-01', '2023-01-31'
		WHERE NOT EXISTS (
		  SELECT * FROM readPlan
		  WHERE bookId = 1 AND finished = 0
		);
		 */
		int result = jdbcTemplate.update(sql,
				readPlan.getBookId(),
				Date.valueOf(readPlan.getStartPlanDate()),
				Date.valueOf(readPlan.getEndPlanDate()),
				readPlan.getBookId()
				);
		
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
	
	// 読書プランの取得(所有本IDから)リスト形式
	public List<ReadPlan> findByBookId(int bookId) {
		String sql="SELECT * FROM READPLAN WHERE BOOKID = ? AND STATUS=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,bookId);
		// System.out.println(resultList);
		List<ReadPlan> readPlanList = new ArrayList<>();
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int readPlanId = (int) result.get("readPlanId");
				LocalDate startPlanDate = ((Date) result.get("startPlanDate")).toLocalDate();
				LocalDate endPlanDate = ((Date) result.get("endPlanDate")).toLocalDate();
				LocalDate startRecordDate = result.get("startRecordDate") == null ?
						null : ((Date) result.get("startRecordDate")).toLocalDate();
				LocalDate endRecordDate = result.get("endtRecordDate") == null ?
						null : ((Date) result.get("endRecordDate")).toLocalDate();
				int finished = (int) result.get("finished");
//				List<ReadRecord> readRecordList = readRecordRepository.findByReadRecordId(readPlanId);
				ReadPlan readPlan = new ReadPlan(
						readPlanId,
						bookId,
						startPlanDate,
						endPlanDate,
						startRecordDate,
						endRecordDate,
						finished/*,
						readRecordList*/
				);
				readPlanList.add(readPlan);
			}
		}
		return readPlanList;
	}
	
	// 読書プランの取得(読書プランIDから)単発
	public ReadPlan findByReadPlanId(int readPlanId) {
		String sql="SELECT * FROM READPLAN WHERE READPLANID = ? AND STATUS=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,readPlanId);
//		System.out.println(resultList);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int bookId = (int) result.get("bookId");
				LocalDate startPlanDate = ((Date) result.get("startPlanDate")).toLocalDate();
				LocalDate endPlanDate = ((Date) result.get("endPlanDate")).toLocalDate();
				LocalDate startRecordDate = result.get("startRecordDate") == null ?
						null : ((Date) result.get("startRecordDate")).toLocalDate();
				LocalDate endRecordDate = result.get("endtRecordDate") == null ?
						null : ((Date) result.get("endRecordDate")).toLocalDate();
				int finished = (int) result.get("finished");
				ReadPlan readPlan = new ReadPlan(
						readPlanId,
						bookId,
						startPlanDate,
						endPlanDate,
						startRecordDate,
						endRecordDate,
						finished/*,
						null*/
				);
				return readPlan;
			}
		}
		return null;
	}

	// 読書プランの終了
	public boolean finished(ReadPlan readPlan) {
		// int readPlanId = readPlan.getReadPlanId();
		String sql="UPDATE readPlan SET finished=1,startRecordDate=?,endRecordDate=? WHERE readPlanId=?";
		int result = jdbcTemplate.update(
				sql,
				readPlan.getStartRecordDate(),
				readPlan.getEndRecordDate(),
				readPlan.getReadPlanId()
				);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;
	}
	
	// 所有者のIDを取得
	public int getOwnerById(int readPlanId) {
		String sql = "select userid from bookShelf where bookid in (select bookId from readPlan where readPlanId = ?)";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,readPlanId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				return (int) result.get("userId");
			}
		}
		return -1;
	}
	
	// 読書プランの論理削除
	public boolean removeByUserId(int userId) {
		String sql="UPDATE readPlan SET status=-1 WHERE bookId IN (SELECT bookId FROM bookShelf WHERE userId=?)";
		int result = jdbcTemplate.update(sql,userId);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
}
