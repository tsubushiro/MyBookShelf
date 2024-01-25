package com.example.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.BookShelf;
import com.example.demo.entity.CommonBook;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.CommonBookService;


@Component
public class UnuseableBookValidator implements Validator {
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	CommonBookService commonBookService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// return BookShelf.class.isAssignableFrom(clazz);
		return true;//上だとエラー
	}

	@Override
	public void validate(Object target, Errors errors) {
		if((target instanceof BookShelf) == false) return;
//		System.out.println("UnusableBookValidatorのバリデータよばれた");
		BookShelf bookShelf = (BookShelf) target;// BookShelfの取得
		// 各フィールド取得
		String commonBookId = bookShelf.getCommonBookId().replaceAll("-", "");//ISBNの取得
		// バリデータの検知の有無によらず、この関数は常に動くので、あやまった入力データで動かないようにする
		if( commonBookId.matches("97[0-9-]{11,}") == false ) {
			errors.rejectValue("commonBookId","","97からはじまる13桁のISBNコードを入力してください");
			return;
		}
		int userId = bookShelf.getUserId();// hiddenのuserIdの取得
//		System.out.println(bookShelf);
		// 各サービス
		CommonBook resultCB = commonBookService.findById(commonBookId);
		BookShelf resultBS = bookShelfService.findById(userId, commonBookId);
		if(resultCB == null ) {
//			System.out.println("ISBNみつからない");
			errors.rejectValue("commonBookId","","該当するISBNコードの本は見つかりません");
		}else if(resultBS != null) {
//			System.out.println("登録済");
			errors.rejectValue("commonBookId","","一つの本棚に同じ本は登録できません。次の本は登録済です。"+commonBookId+":"+resultCB.getTitle());
		}else {
//			System.out.println("OK");
		}
	}	
}
