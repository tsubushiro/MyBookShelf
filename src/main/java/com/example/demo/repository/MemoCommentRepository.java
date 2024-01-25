package com.example.demo.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.MemoComment;

// メモ管理
@Repository
public class MemoCommentRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	// メモの検索
	public MemoComment findById(int memoCommentId) {
		String sql="SELECT * FROM memoComment WHERE memoCommentId = ? and status=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,memoCommentId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int bookId = (int) result.get("bookId");
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");//可変長に対応
				LocalDateTime recordDate =LocalDateTime.parse(result.get("recordDate").toString(),dtf); //SQLの結果を文字列にしてパースしている
				String text = (String) result.get("text");
				String title = (String) result.get("title");
				MemoComment memoComment = new MemoComment(
						memoCommentId,
						bookId,
						title,
						text,
						recordDate
				);
				return memoComment;
			}
		}
		return null;
	}
	
	// メモの登録
	public boolean insert(MemoComment memoComment) {
		String sql="INSERT INTO memoComment(bookId,title,text,recordDate) values(?,?,?,?)";
		int	result = jdbcTemplate.update(sql,
				memoComment.getBookId(),
				memoComment.getTitle(),
				memoComment.getText(),
				Timestamp.valueOf(LocalDateTime.now())
				);
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
	
	// メモの更新
	public boolean update(MemoComment memoComment) {
		String sql="UPDATE memoComment SET title = ? , text = ? ,recordDate = ? "
				+ " WHERE memoCommentId=? and status=0";
		int result = jdbcTemplate.update(sql,
				memoComment.getTitle(),
				memoComment.getText(),
				Timestamp.valueOf(LocalDateTime.now()),
				memoComment.getMemoCommentId()
				);
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功		
	}
	
	// メモのリストの取得
	public List<MemoComment> findByBookId(int bookId) {
//		System.out.println("よんだ？");
		String sql="SELECT * FROM memoComment WHERE bookId = ? AND status=0 ORDER BY title";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,bookId);
//		System.out.println(resultList);
		List<MemoComment> memoCommentList = new ArrayList<>();
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int memoCommentId = (int) result.get("memoCommentId");
				// ミリ秒日時　 https://imakaramegane.hatenablog.com/entry/2018/08/19/231417
//				DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSSSSS");//普通
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");//可変長に対応
				LocalDateTime recordDate =LocalDateTime.parse(result.get("recordDate").toString(),dtf); //SQLの結果を文字列にしてパースしている
				String text = (String) result.get("text");
				String title = (String) result.get("title");
				MemoComment memoComment = new MemoComment(
						memoCommentId,
						bookId,
						title,
						text,
						recordDate
				);
				memoCommentList.add(memoComment);
			}
		}
		return memoCommentList;
	}
	
	// 所有者のIDを取得
	public int getOwnerById(int memoCommentId) {
		String sql = "select userid from bookshelf "
				+ " where bookid in (select bookid from memoComment "
				+ " where memoCommentId=?)";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,memoCommentId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				return (int) result.get("userId");
			}
		}
		return -1;
	}
	
	// ユーザーに紐づいたメモの削除
	public boolean removeByUserId(int userId) {
		String sql="UPDATE memoComment SET status=-1"
				+ " where bookid in ( select bookid from bookShelf "
				+ " where userId=?) ";
		int result = jdbcTemplate.update(sql,userId);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
	
	// メモの削除
	public boolean removeById(int memoComment) {
		String sql="UPDATE memoComment SET status=-1"
				+ " where memoCommentId=? ";
		// System.out.println(sql);
		int result = jdbcTemplate.update(sql,memoComment);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
	
	// ユーザ所有の最新のメモID
	public int findLastIdByBookId(int bookId) {
//		System.out.println("よんだ？");
//		String sql = "select max(memoCommentId) as last from memoComment join bookShelf using(bookid) "
//				+ " where bookId= ?";
		String sql = "select max(memoCommentId) as last from memoComment where status=0 and bookid = ?";
//		String sql = "select max(memoCommentId) as last from memoComment as m join bookShelf using(bookid) "
//				+ " where bookId= ? and m.status=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,bookId);
		try {
			// メモがすべて削除されている場合のためtryの中にいれた
			if(resultList.size()!=0 && resultList != null) {
				for(Map<String,Object> result :resultList) {
					return (int) result.get("last");
				}
			}
		}
		catch(Exception e) {
			e.getStackTrace();
		}
		return -1;
	}
}
