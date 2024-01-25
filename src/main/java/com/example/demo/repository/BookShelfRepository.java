package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.CommonBook;

@Repository
public class BookShelfRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	// ISBNコードの本をユーザの本棚に登録する
	public boolean insert(Account registerUser,CommonBook commonBook) {
		// 情報取得
		int userId = registerUser.getUserId();
		String isbn = commonBook.getCommonBookId();
		return insert(userId,isbn);
	}
	
	// ISBNコードの本をユーザの本棚に登録する
	public boolean insert(int userId,String isbn){
		// 978- みたいな場合があるのでハイフン削除
		isbn = isbn.replaceAll("-", "");
		if( userId < 1) return false;
		String sql ="INSERT INTO BOOKSHELF(USERID,COMMONBOOKID) VALUES(?,?)";
		int result = jdbcTemplate.update(sql,
				userId,
				isbn);
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
	
	// ISBNコードからユーザの本棚の所有本を取得する
	public BookShelf findById(int userId,String isbn) {
		// 978- みたいな場合があるのでハイフン削除
		isbn = isbn.replaceAll("-", "");
		String sql = "SELECT * FROM BOOKSHELF WHERE USERID = ? AND COMMONBOOKID = ?";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,userId,isbn);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int bookId = (int) result.get("bookId");
				int rank = (int) result.get("rank");
				String tag = (String) result.get("tag");			
				int privateBook = (int) result.get("privateBook");
				BookShelf bookShelf = new BookShelf(bookId,userId,isbn,rank,tag,privateBook);
				return bookShelf;
			}
		}
		return null;
	}
	
	// 所有本IDから本の情報を取得
	public BookShelf findByBookId(int bookId) {
		String sql = "SELECT * FROM BOOKSHELF WHERE BOOKID = ?";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,bookId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int userId = (int) result.get("userId");
				String commonBookId = (String) result.get("commonBookId");
				int rank = (int) result.get("rank");
				String tag = (String) result.get("tag");			
				int privateBook = (int) result.get("privateBook");
				BookShelf bookShelf = new BookShelf(bookId,userId,commonBookId,rank,tag,privateBook);
				return bookShelf;
			}
		}
		return null;
	}
	
	// 本棚の全リストを取得
	public List<BookShelf> findAll(String where){
		String sql = "SELECT * FROM BOOKSHELF "+where;
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
		List<BookShelf> bookShelfList = new ArrayList<>();
		for(Map<String, Object> result : resultList) {
			int bookId = (int) result.get("bookId");
			int userId = (int) result.get("userId");
			String commonBookId = (String) result.get("commonBookId");
			int rank = (int) result.get("rank");
			String tag = (String) result.get("tag");			
			int privateBook = (int) result.get("privateBook");
			BookShelf bookShelf = new BookShelf(bookId,userId,commonBookId,rank,tag,privateBook);
			bookShelfList.add(bookShelf);
		}
		return bookShelfList;
	}
	
	// 本棚全リストを取得
	public List<BookShelf> findAll(){
		return findAll("");
	}
	
	// 所有者のIDを取得
	public int getOwnerById(int bookId) {
		String sql = "SELECT userId FROM bookShelf WHERE bookId=? AND status=0";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,bookId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				return (int) result.get("userId");
			}
		}
		return -1;
	}
	
	// 所有本の論理削除
	public boolean removeByUserId(int userId) {
		String sql="UPDATE bookShelf SET STATUS=-1 WHERE USERID=?";
		int result = jdbcTemplate.update(sql,userId);
		if( result == 0 ) {
			return false;//削除失敗
		}
		return true;//削除成功
	}
	
	
	// 本棚を持っているユーザの一覧
	public List<Integer> getAllUserListHasBook(){
		String sql ="select userId from account where userId in (select userId from bookShelf)";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
		List<Integer> userList = new ArrayList<>();
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				 userList.add((int) result.get("userId"));
			}
		}
		return userList;
	}
}
