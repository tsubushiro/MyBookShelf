package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.CommonBook;
import com.example.demo.repository.BookShelfRepository;

@Service
public class BookShelfService{
	
	@Autowired
	BookShelfRepository repository;
	
	// ISBNコードの本をユーザの本棚に登録する
	public boolean insert(Account registerUser,CommonBook commonBook) {
		return repository.insert(registerUser, commonBook);
	}
	
	// ISBNコードの本をユーザの本棚に登録する
	public boolean insert(int userId,String isbn){
		return repository.insert(userId,isbn);
	}
	
	// ISBNコードからユーザの本棚の所有本を取得する
	public BookShelf findById(int userId,String isbn) {
		return repository.findById(userId, isbn);
	}
	
	// 所有本IDから本の情報を取得
	public BookShelf findByBookId(int bookId){
		return repository.findByBookId(bookId);
	}
	
	// ユーザの全所有本リストを取得
	public List<BookShelf> findAllFromBookShelf(int userId){
		return repository.findAll("where STATUS=0 AND USERID="+userId);
	}
	
	// ユーザの全所有本リストを取得
	public List<BookShelf> findAllFromBookShelf(Account registerUser){
		return repository.findAll("where STATUS=0 AND USERID="+registerUser.getUserId());
	}
	
	// 本棚を持っているユーザの一覧
	public List<Integer> getAllUserListHasBook(){
		return repository.getAllUserListHasBook();
	}
}