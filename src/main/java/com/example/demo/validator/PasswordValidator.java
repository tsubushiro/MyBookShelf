package com.example.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.PasswordEdit;
import com.example.demo.service.UserService;

// パスワードチェック用バリデータ
@Component
public class PasswordValidator implements Validator{

	@Autowired
	UserService service;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// return Account.class.isAssignableFrom(clazz);
		return true;
	}

	// パスワードの確認
	@Override
	public void validate(Object target, Errors errors) {
		if((target instanceof PasswordEdit) == false) return;
//		System.out.println("バリデータよばれた");
		PasswordEdit passwordEdit = (PasswordEdit) target;
		System.out.println(passwordEdit.getNewPass()+","+passwordEdit.getNewPass2());
		if(passwordEdit.getNewPass().equals(passwordEdit.getNewPass2()) == false) {
			errors.rejectValue("newPass2","","新しいパスワードと確認用パスワードが一致しません。もう一度入力してください。");
			return;
		}
	}

}
