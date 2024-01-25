package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CommonBook;
import com.example.demo.repository.CommonBookRepository;

//isbnを引数にして、共通本データを戻す
@Service
public class CommonBookService {
	
	@Autowired
	CommonBookRepository repository;
	
	public CommonBook findById(String isbn) {
		CommonBook result;
		if(isbn == null) return null; 
		// 978- みたいな場合があるのでハイフン削除
		isbn = isbn.replaceAll("-", "");
//		System.out.println(isbn);
		// COMMONBOOK表内の共通本データを取得
		result = repository.findById(isbn);
		// 表内から取得できた場合は共通本データを戻す
		if( result != null ) {
			return result;
		}
//		System.out.println("表になし");
		// できない場合GoogleBooksAPIに問い合わせる
		result = repository.findByIdFromWebAPI(isbn);
		if ( result == null ) {
			// GoogleBooksAPIからも取得できない場合はnull
			return null;
		}else {
//			System.out.println("GoogleBooksAPIから取得");
			// GoogleBooksAPIから取得した本をCOMMONBOOK表に登録
			boolean insertCheck = repository.insert(result);
			if( insertCheck == true) {
				result = repository.findById(isbn);//登録したあと再び取得
				return result;
			}else {
				return null;
			}
		}
	}
}
