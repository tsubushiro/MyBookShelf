package com.example.demo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.LoginUser;

@Repository
public class AccountRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
   //nameとpassが、DBに存在するかを確認
	// public LoginUser findAccount(LoginUser loginUser) {
		
	// ユーザ取得(呼ぶ側でパスワードチェックするのでwhereにパスワードを除外）
	public Account findAccount(LoginUser loginUser) {
		String sql="SELECT USERID,NAME,PASS,AGE,MAIL FROM ACCOUNT WHERE NAME = ? AND STATUS = 0";
		System.out.println(loginUser);
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,loginUser.getName());
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				int userId = (int) result.get("userid");
				String name = (String) result.get("name");
				String pass = (String) result.get("pass");
				int age = (int) result.get("age");
				String mail = (String) result.get("mail");
				Account registerUser = new Account(userId,name,pass,age,mail);
				System.out.println(registerUser);
				return registerUser;
			}
		}
		return null;
	}

	// 既存ユーザがあるかどうか？(名前のみ)
	public boolean findAccount(Account registerUser) {
		String sql="SELECT COUNT(*) FROM ACCOUNT WHERE NAME = ?";
		int result ;
		result = jdbcTemplate.queryForObject(
				sql, 
				Integer.class,
				registerUser.getName());
		// System.out.println("result:"+result);
		if( result == 0) {
			return false;
		}
		return true;
	}
	
	// 同じユーザ名のIDを返す
	public int findUserName(String name) {
		String sql="SELECT USERID FROM ACCOUNT WHERE NAME = ?";
		int result = -1;
		try {
			result = jdbcTemplate.queryForObject(
					sql, 
					Integer.class,
					name);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result;
	}
	// ユーザ登録
	public boolean insert(Account registerUser) {
		// System.out.println("insert");
		String sql="INSERT INTO ACCOUNT(NAME,PASS,AGE,MAIL) VALUES(?,?,?,?)";
		int result = jdbcTemplate.update(
				sql,
				registerUser.getName(),
				registerUser.getPass(),
				registerUser.getAge(),
				registerUser.getMail());
		System.out.println("result:"+result);
		if( result == 0) {
			return false;//登録失敗
		}
		return true;//登録成功
	}
	// ユーザ削除
	public boolean remove(Account registerUser) {
		String sql="UPDATE ACCOUNT SET STATUS=-1 WHERE USERID=?";
		// System.out.println(registerUser);
		int result = jdbcTemplate.update(sql,
				registerUser.getUserId()
				);
//		String sql="UPDATE ACCOUNT SET STATUS=-1 WHERE USERID=? AND PASS=?";
//		System.out.println(registerUser);
//		int result = jdbcTemplate.update(sql,
//				registerUser.getUserId(),
//				registerUser.getPass()
//				);
		// System.out.println(result);
		if( result == 0 ) {
			return false;//削除失敗
		}
		// System.out.println("削除成功");
		return true;//削除成功
	}
	// ユーザ更新
	public Account update(Account registerUser) {
		String sql="UPDATE ACCOUNT SET NAME = ? ,PASS = ? ,AGE = ? ,MAIL=? WHERE USERID=?";
		System.out.println(registerUser.getName()+"\n"+
				registerUser.getPass()+"\n"+
				registerUser.getAge()+"\n"+
				registerUser.getMail()+"\n"+
				registerUser.getUserId());
		int result = jdbcTemplate.update(
				sql,
				registerUser.getName(),
				registerUser.getPass(),
				registerUser.getAge(),
				registerUser.getMail(),
				registerUser.getUserId()
			);
		
		if( result == 0 ) {
			return null;//削除失敗
		}
		return registerUser;//削除成功
	}
	
	// ユーザ名取得
	public String getUserName(int userId) {
		String sql = "select name from account where userId=?";
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,
				userId);
		if(resultList.size()!=0 && resultList != null) {
			for(Map<String,Object> result :resultList) {
				return (String) result.get("name");
			}
		}
		return null;
	}

	// 入力パスワード
}
