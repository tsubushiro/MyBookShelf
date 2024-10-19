package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.LoginUser;

public interface UserService {
	Account loginCheck(LoginUser loginUser);
	boolean registerCheck(Account registerUser);
	Account createNewAccount(Account registerUser);
	// boolean registerAccount(Account registerUser);
	boolean removeAccount(Account registerUser);
	Account updateAccount(Account registerUser,boolean withoutPass);
	int findUserName(String name);
	String getUserName(int userId);
}
