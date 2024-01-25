package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AdditionalInfo;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.BookView;
import com.example.demo.entity.CommonBook;

@Service
public class BookViewService {
	
	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	AdditionalInfoService additionalInfoService;
	
	// 所有本を本情報へ変換
	public BookView convertFromBookShelf(BookShelf bookShelf) {
		String commonBookId = bookShelf.getCommonBookId();
		CommonBook commonBook = commonBookService.findById(commonBookId);
		int bookId = bookShelf.getBookId();
		String title = commonBook.getTitle();
		String authors = commonBook.getAuthors();
		String publisher = commonBook.getPublisher();
		int pageCount = commonBook.getPageCount();
		String thumbnail = commonBook.getThumbnail();
		AdditionalInfo additionalInfo = additionalInfoService.findById(bookId);
		BookView bookView = new BookView(
				bookId, // 所有本ID 
				commonBookId, // 共通本ID
				title, // タイトル
				authors, // 著者
				publisher, // 出版社
				pageCount, // ページ数
				thumbnail, // サムネイル
				additionalInfo //付帯情報
		);
		return bookView;
	}
	
	// 所有本IDから本情報を取得
	public BookView findById(int bookId) {
		BookShelf bookShelf = bookShelfService.findByBookId(bookId);
		return convertFromBookShelf(bookShelf);
		
//		BookShelf bookShelf = bookShelfService.findByBookId(bookId);
//		String commonBookId = bookShelf.getCommonBookId();
//		CommonBook commonBook = commonBookService.findById(commonBookId);
//		String title = commonBook.getTitle();
//		String authors = commonBook.getAuthors();
//		String publisher = commonBook.getPublisher();
//		int pageCount = commonBook.getPageCount();
//		String thumbnail = commonBook.getThumbnail();
//		AdditionalInfo additionalInfo = additionalInfoService.findById(bookId);
//		BookView bookView = new BookView(
//				bookId, // 所有本ID 
//				commonBookId, // 共通本ID
//				title, // タイトル
//				authors, // 著者
//				publisher, // 出版社
//				pageCount, // ページ数
//				thumbnail, // サムネイル
//				additionalInfo //付帯情報
//		);
//		return bookView;
	}
}
