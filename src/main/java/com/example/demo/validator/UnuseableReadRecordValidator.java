package com.example.demo.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.ReadPlanView;
import com.example.demo.entity.ReadRecord;
import com.example.demo.service.ReadPlanViewService;

@Component
public class UnuseableReadRecordValidator implements Validator {
	@Autowired
	ReadPlanViewService readPlanViewService;
	
	@Override
	public boolean supports(Class<?> clazz) {
//		System.out.println("よんだ？");
		// return Account.class.isAssignableFrom(clazz);
		return true;
	}

	// ページ数のバリデーション
	// ・最終ページを超える
	@Override
	public void validate(Object target, Errors errors) {
//		System.out.println("UnuseableReadRecordValidatorバリデートよばれた！");
		if((target instanceof ReadRecord) == false) return;
        ReadRecord readRecord = (ReadRecord) target;
        int readPlanId = readRecord.getReadPlanId();
		// 読書プラン表示
		ReadPlanView readPlanView = readPlanViewService.findById(readPlanId);
        // 最終ページ超えてないか？
		int readPage = readRecord.getReadPage();
		int pageCount = readPlanView.getCommonBook().getPageCount();
		if(readPage > pageCount){
			errors.rejectValue("readPage","","本のページ数:"+pageCount+"を超えています！");
		}
	}
}