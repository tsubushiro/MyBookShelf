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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.Owner;
import com.example.demo.entity.ReadPlan;
import com.example.demo.entity.ReadPlanView;
import com.example.demo.entity.ReadRecord;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.BookViewService;
import com.example.demo.service.CommonBookService;
import com.example.demo.service.OwnerService;
import com.example.demo.service.PlotDataService;
import com.example.demo.service.ReadPlanService;
import com.example.demo.service.ReadPlanViewService;
import com.example.demo.service.ReadRecordService;
import com.example.demo.validator.UnuseableReadRecordValidator;

import jakarta.servlet.http.HttpSession;

// 読書履歴処理
@Controller
public class ReadRecordController {
	
	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	BookViewService bookViewService;
	
	@Autowired
	ReadRecordService readRecordService;
	
	@Autowired
	ReadPlanViewService readPlanViewService;
	
	@Autowired
	ReadPlanService readPlanService;
	
	@Autowired
	PlotDataService plotDataService;
	
	@Autowired
	OwnerService ownerService;
	
	@Autowired
	UnuseableReadRecordValidator unuseableReadRecordValidator;
	
	@InitBinder
	public void validatorBinder(WebDataBinder binder) {
		binder.addValidators(unuseableReadRecordValidator);
	}
	
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
	// インスタンスをモデルに格納
	@ModelAttribute("readPlanView")
	public ReadPlanView setReadPlanView() {
//		System.out.println("よばれた");
		return new ReadPlanView();
	}
	// インスタンスをモデルに格納
	@ModelAttribute("readRecord")
	public ReadRecord setReadRecord() {
//		System.out.println("よばれた");
		return new ReadRecord();
	}
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/showReadPlan/{readPlanId}")
	public String showReadPlanView(Model model,
			@PathVariable int readPlanId) {
		// System.out.println("readPlanId:" + readPlanId);
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		// 読書プラン表示
		ReadPlanView readPlanView = readPlanViewService.findById(readPlanId);
		model.addAttribute("readPlanView", readPlanView);
		int userId = account.getUserId();
		
		// 閲覧している読書プランのオーナー
		int ownerId = ownerService.getReadPlanOwnerById(readPlanId);
		
		// 閲覧物のオーナー
		Owner owner = ownerService.getOwnerByUserId(ownerId);
		model.addAttribute("owner", owner);
		model.addAttribute("isOwner", (ownerId == userId));// 閲覧物はユーザのものか？
		
		// 既読ページ
		int readPageMax = 0;
		for(ReadRecord readRecord:readPlanView.getReadRecordList()) {
			readPageMax = Math.max(readRecord.getReadPage(),readPageMax);
		}
		// 読書登録用フォーム
		ReadRecord readRecord = new ReadRecord(
				0,
				readPlanView.getReadPlanId(),
				LocalDate.now(),
				readPageMax
				);
		model.addAttribute("readRecord", readRecord);
		// グラフのプロットデータ
		String recordPlanPlotData = plotDataService.getPlotDataJSON(readPlanId);
		model.addAttribute("recordPlanPlotData", recordPlanPlotData);

		// 所有本
		int bookId = readPlanView.getBookId();
		// 読書プランの進捗を取得
		int readProgress = plotDataService.getProgress(bookId);
		model.addAttribute("readProgress",readProgress);

		return "showReadPlan";
	}
	
	// 読書履歴の登録
	@PostMapping("registNewReadRecord")
	public String postRegistNewReadRecord(@Validated ReadRecord readRecord,
			BindingResult bindingResult,Model model){
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		model.addAttribute("readRecord",readRecord);
		int readPlanId = readRecord.getReadPlanId();
		// 読書プラン表示
		ReadPlanView readPlanView = readPlanViewService.findById(readPlanId);
		model.addAttribute("readPlanView", readPlanView);
		if(bindingResult.hasErrors()) {	
			System.out.println("エラーあるよ");
			return "showReadPlan";//リダイレクトはうまくいかない！
		}
		// エラーはない
		boolean result = false;
		try {
			// 読書履歴の登録
			result = readRecordService.insert(readRecord);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(result != true) {
			// System.out.println("登録失敗しました！");
			bindingResult.rejectValue("","","登録失敗しました！値をいれなおしてください");
			return "showReadPlan";
		}
		int readPage = readRecord.getReadPage();
		int pageCount = readPlanView.getCommonBook().getPageCount();
		// 読了するかどうか
		if(readPage >= pageCount){
			ReadPlan readPlan = readPlanService.findByReadPlanId(readPlanId);
			result = false;
			// 読了処理
			try {
				result = readPlanService.finished(readPlan);
				session.setAttribute("finishedMessage","読了しました！お疲れ様です！");
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			if(result != true) {
				bindingResult.rejectValue("","","読了処理に失敗しました！");
				return "showReadPlan";
			}
			return "redirect:/showBook/"+readPlan.getBookId() ;//ページへ
		}else{
			return "redirect:/showReadPlan/"+readPlanId;
		}
	}
	
	@GetMapping("/deleteReadRecord/{readPlanId}")
	public String showReadPlanView(Model model,
			@PathVariable int readPlanId,@RequestParam int readRecordId) {
		// System.out.println("readPlanId:"+readPlanId);
		// System.out.println("readRecordId:"+readRecordId);
		// アカウント
		Account account = (Account) session.getAttribute("account");
		int userId = account.getUserId();
		if(ownerService.isOwnerReadPlan(readPlanId, userId)) {
			boolean result = readRecordService.removeById(readRecordId);
			if(result == true) {
				return "redirect:/showReadPlan/"+readPlanId;
			}else {
				return "failure";
			}
		}
		return "failure";
	}
	
	@ExceptionHandler(Exception.class)
    public String error(Exception e) {
        // エラー用のページに遷移させる
        return "failure";
    }
}
