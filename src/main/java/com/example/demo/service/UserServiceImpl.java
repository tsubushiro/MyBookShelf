package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.LoginUser;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BookShelfRepository;
import com.example.demo.repository.MemoCommentRepository;
import com.example.demo.repository.ReadPlanRepository;
import com.example.demo.repository.ReadRecordRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	AccountRepository repository;
	
	@Autowired
	BookShelfRepository bookShelfRepository;
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Autowired
	ReadRecordRepository readRecordRepository;
	
	@Autowired
	MemoCommentRepository memoCommentRepository;
	
	@Override
	public Account loginCheck(LoginUser loginUser) {
		Account result = repository.findAccount(loginUser);
		return result;
	}

	@Override
	public boolean registerCheck(Account registerUser) {
		boolean result = repository.findAccount(registerUser);
		return result;
	}

	@Override
	public boolean createNewAccount(Account registerUser) {
		boolean result = repository.insert(registerUser);
		return result;
	}
//	@Override
//	public boolean registerAccount(Account registerUser) {
//		if(repository.findAccount(registerUser)){
//			return false;
//		}
//		boolean result = repository.insert(registerUser);
//		return result;
//	}

	// アカウントの削除
	@Override
	public boolean removeAccount(Account registerUser) {
		int UserId = registerUser.getUserId();
		if(repository.findAccount(registerUser) == false){ //見つからなかったら抜ける
			// System.out.println("なかったよ");
			return false;
		}
		boolean result = repository.remove(registerUser);
		// System.err.println("result:"+result);
		
		// 各データ削除
		readRecordRepository.removeByUserId(UserId);// 読書記録
		readPlanRepository.removeByUserId(UserId);// 読書プラン
		bookShelfRepository.removeByUserId(UserId);// 本棚
		memoCommentRepository.removeByUserId(UserId);// メモ
		
		return result;
	}

	@Override
	public Account updateAccount(Account registerUser) {
		Account result = repository.update(registerUser);
		return result;
	}

	@Override
	public int findUserName(String name) {
		// TODO 自動生成されたメソッド・スタブ
		// 同じユーザの名前を取得
		int result = repository.findUserName(name);
		return result;
	}

	@Override
	public String getUserName(int userId) {
		// TODO 自動生成されたメソッド・スタブ
		String result = repository.getUserName(userId); 
		return result;
	}
	
}
