package com.example.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.Account;
import com.example.demo.service.UserService;

// 同じnameを禁止するためのバリデータ
@Component
public class UnuseableNameValidator implements Validator{

	@Autowired
	UserService service;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// return Account.class.isAssignableFrom(clazz);
		return true;
	}

	// 名前のバリデート（登録名が同じ名前の場合は拒否する)
	// ・新規登録の場合、他のユーザの名前と重複した時拒否
	// ・（ユーザ情報編集における）名前編集の場合、
	//  編集画面から取得した名前が違うuserIdの名前と重複したら拒否 　
	@Override
	public void validate(Object target, Errors errors) {
		if((target instanceof Account) == false) return;
		// System.out.println("バリデータよばれた");
		Account account = (Account) target;
		int userId = account.getUserId(); // hiddenのuserIdの取得
		// System.out.println("userId:"+userId);
		// if( userId == 0 ) return; // userId(hidden)に値が入ってないのでスルー
		String name = account.getName();
		// System.out.print("name:"+name);
		// IDを取得
		int findId = service.findUserName(name);
		if(findId <= 0 ) return; // 見つからない場合は-1
		// System.out.println("findId:"+findId);
		if( userId == findId ) {
//			System.out.println("OK");
			// return;
		}else {
//			System.out.println("error");
			errors.rejectValue("name","","名前："+name+"は登録済です");
		}
	}

}
