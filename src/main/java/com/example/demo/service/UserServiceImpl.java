package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	
	// パスワードハッシュ化
	/*
	 BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
	 // ハッシュ化
	 String encodeedPassword = bcpe.encode(user.getPassword());
	*/
	
	// パスワード認証
	/*
	 BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
	// 入力されたパスワードとDBのパスワード(ハッシュ化済み)を比較
	if (bcpe.matches(user.getPassword(), compUser.getPassword())) {
		loginUser.setUser(compUser);
		return true;
	}
	*/
	// パスワードハッシュ化に伴い変更
	@Autowired
	private PasswordEncoder passwordEncoder;
	
//    // コンストラクタ
//    public UserServiceImpl() {
//    	 passwordEncoder = new BCryptPasswordEncoder();
//    }
//    
	//　パスワードハッシュ化に伴い変更 
	@Override
	public Account loginCheck(LoginUser loginUser) {
		String inputPass = loginUser.getPass();//入力パスワード
		Account result = null ;
		Account account = repository.findAccount(loginUser);
		// 入力されたパスワードとDBのパスワード(ハッシュ化済み)を比較
		if(account!=null && passwordEncoder.matches(inputPass, account.getPass())) {
			account.setPass(inputPass);//アカウントを返す時はパスワードも返却
			result = account;
		}
		return result;
	}

	@Override
	public boolean registerCheck(Account registerUser) {
		boolean result = repository.findAccount(registerUser);
		return result;
	}

	@Override
	public boolean createNewAccount(Account registerUser) {
		// パスワードハッシュ化
		registerUser = accountClone(registerUser);
		registerUser.setPass(passwordEncoder.encode(registerUser.getPass()));;
		// registerUser.setPass((registerUser.getPass()));
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
		// アカウントの妥当性の確認
		LoginUser loginUser = new LoginUser( registerUser.getName(),registerUser.getPass());
		String inputPass = registerUser.getPass();//入力パスワード
		Account account = loginCheck(loginUser);//ハッシュ化パスワード取得のため
		// 同じIDでない場合は異常
		if(account.getUserId() != registerUser.getUserId()) return false;
		// 入力されたパスワードとDBのパスワード(ハッシュ化済み)を比較
		if(!(account !=null && passwordEncoder.matches(inputPass, account.getPass()))) {
			return false;// パスワードが一致しない場合は抜ける
		}
		// System.out.println(registerUser);
		boolean result = repository.remove(registerUser);
		if(result == false) {
			return false;
		}else {
			// 各データ削除
			readRecordRepository.removeByUserId(UserId);// 読書記録
			readPlanRepository.removeByUserId(UserId);// 読書プラン
			bookShelfRepository.removeByUserId(UserId);// 本棚
			memoCommentRepository.removeByUserId(UserId);// メモ
			
			return true;
		}
	}

	@Override
	public Account updateAccount(Account registerUser) {
		String inputPass = registerUser.getPass();//元のパスワード
		// パスワードハッシュ化
		Account result = null;
		registerUser = accountClone(registerUser);
		registerUser.setPass(passwordEncoder.encode(registerUser.getPass()));
		Account account = repository.update(registerUser);
		if(account!=null) {
			account.setPass(inputPass); //元のパスワード
			result = account;
		}
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
	
	// アカウントのクローン
	private Account accountClone(Account account) {
		return new Account(
				account.getUserId(),
				account.getName(),
				account.getPass(),
				account.getAge(),
				account.getMail()
				)
		;
	}
}
