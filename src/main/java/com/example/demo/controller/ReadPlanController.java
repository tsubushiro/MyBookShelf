package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.BookView;
import com.example.demo.entity.ReadPlan;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.BookViewService;
import com.example.demo.service.CommonBookService;
import com.example.demo.service.PlotDataService;
import com.example.demo.service.ReadPlanService;
import com.example.demo.validator.UnuseableReadPlanValidator;

import jakarta.servlet.http.HttpSession;

// 読書プラン処理
@Controller
public class ReadPlanController {
	
	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	BookViewService bookViewService;
	
	@Autowired
	ReadPlanService readPlanService;
	
	@Autowired
	PlotDataService plotDataService;
	
	@Autowired
	UnuseableReadPlanValidator validator;
	
	@Autowired
	private HttpSession session;
	
	// インスタンスをモデルに格納
	@ModelAttribute("account")
	public Account setUpAccount() {
//		System.out.println("よばれた");
		return new Account();
	}
	// インスタンスをモデルに格納
	@ModelAttribute("bookShelf")
	public BookShelf setBookShelf() {
//		System.out.println("よばれた");
		return new BookShelf();
	}	
		
	@InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }	

	@GetMapping("registReadPlan/{bookId}")
	public String registReadPlanView(Model model,
			@PathVariable int bookId) {
//		System.out.println("@GetMapping(\"registReadPlan/"+bookId+"\")");
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		
		// 日付
		LocalDate startDate = LocalDate.now();//システム日付
		LocalDate endDate = startDate.plusWeeks(2);//2週間後
		
		// 読書プラン
		ReadPlan readPlan = new ReadPlan(
				0,
				bookId,
				startDate,
				endDate,
				null,
				null,
				0/*,
				null*/);
		model.addAttribute("readPlan", readPlan);
		// 本情報
		BookView bookView = bookViewService.findById(bookId);
		model.addAttribute("bookView", bookView);
		
		// 進捗状況
		return "registReadPlan";
	}
	
	@PostMapping("registReadPlanOK")
	public String postRegistReadPlanView(@Validated ReadPlan readPlan,
			BindingResult bindingResult,Model model) {
		System.out.println("registReadPlanOK");
//		System.out.println(readPlan);
		int bookId = readPlan.getBookId();
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		// 本情報
		BookView bookView = bookViewService.findById(bookId);
		model.addAttribute("bookView", bookView);
		// 読書プラン
		model.addAttribute("readPlan", readPlan);
		
		if(bindingResult.hasErrors()) {	
			System.out.println("エラーあるよ");
			return "registReadPlan";
		}
		// 読書プランを追加
		boolean result = readPlanService.insert(readPlan);
		if(result == false) {
			bindingResult.rejectValue("", "", "読書プランを追加できませんでした"); 
			return "registReadPlan";
		}
		return "redirect:/showBook/"+bookId;
	}
	
	@ExceptionHandler(Exception.class)
    public String error(Exception e) {
        // エラー用のページに遷移させる
        return "failure";
    }
}
