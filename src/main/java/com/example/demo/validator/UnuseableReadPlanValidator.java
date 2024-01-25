package com.example.demo.validator;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.ReadPlan;
import com.example.demo.repository.ReadPlanRepository;

@Component
public class UnuseableReadPlanValidator implements Validator {
	
	@Autowired
	ReadPlanRepository readPlanRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
//		System.out.println("よんだ？");
		// return Account.class.isAssignableFrom(clazz);
		return true;
	}

	// 開始日と終了日のバリデーションや読了でない読書プランの☐
	@Override
	public void validate(Object target, Errors errors) {
		// System.out.println("UnuseableReadPlanValidatorバリデートよばれた！");
		if((target instanceof ReadPlan) == false) return;
        ReadPlan readPlan = (ReadPlan) target;
        // 開始日と終了日の前後
        LocalDate startPlanDate = readPlan.getStartPlanDate();
        LocalDate endPlanDate = readPlan.getEndPlanDate();
		if(startPlanDate.isAfter(endPlanDate)){
		//	System.out.println("ここ！");
			errors.rejectValue("startPlanDate","","開始予定日["+startPlanDate+"]が終了予定日["+endPlanDate+"]より後の日になっています");
		}
		// 既存読書プランのチェック
		int bookId = readPlan.getBookId();
		List<ReadPlan> readPlanList = readPlanRepository.findByBookId(bookId);
		for(ReadPlan readPlanElem:readPlanList) {
			if(readPlanElem.getFinished() == 0) {
				errors.rejectValue("bookId","","読了してない読書プランが存在しています");
				break;
			}
		}
	}
}