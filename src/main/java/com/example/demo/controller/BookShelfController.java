package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.example.demo.entity.BookShelfView;
import com.example.demo.entity.BookView;
import com.example.demo.entity.CommonBook;
import com.example.demo.entity.Owner;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.BookViewService;
import com.example.demo.service.CommonBookService;
import com.example.demo.service.OwnerService;
import com.example.demo.service.PlotDataService;
import com.example.demo.service.ReadPlanService;
import com.example.demo.service.UserService;
import com.example.demo.validator.UnuseableBookValidator;

import jakarta.servlet.http.HttpSession;

// 本棚処理
@Controller
public class BookShelfController {
	
	@Autowired
	CommonBookService commonBookService;
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	UnuseableBookValidator validator;
	
	@Autowired
	BookViewService bookViewService;
	
	@Autowired
	ReadPlanService readPlanService;
	
	@Autowired
	OwnerService ownerService;
	
	@Autowired
	PlotDataService plotDataService;
	
	@Autowired
	UserService userService;
	
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
	// ↑コントローラ間で遷移するとき必要ぽい
	
	@InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }
	
	@Autowired
	private HttpSession session;	
	
	// 自分の本棚へ遷移
	@GetMapping("top")
	public String getRegisterView(Model model) {
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);//おまじない
		int ownerId = account.getUserId();
		return "redirect:/bookShelf/"+ownerId;
	}
	
	
	@GetMapping("bookShelf/{ownerId}")
	public String bookShelfView(Model model,@PathVariable int ownerId) {
		System.out.println("@GetMapping(\"top\")");
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();
		// 閲覧物のオーナー
		Owner owner = ownerService.getOwnerByUserId(ownerId);
		model.addAttribute("owner", owner);
		model.addAttribute("isOwner", (ownerId == userId));// 閲覧物はユーザのものか？
		// 本棚
		List<BookShelf> bookShelfList = new ArrayList<>();//本棚を入れるリスト
		bookShelfList = bookShelfService.findAllFromBookShelf(ownerId);
// 		bookShelfList = bookShelfService.findAllFromBookShelf(account.getUserId());
		bookShelfList.sort((a,b)->(b.getBookId()-a.getBookId()));// IDでソート
//		System.out.println(bookShelfList);
//		List<CommonBook> commonBookList = new ArrayList<>();
		List<BookView> bookViewList = new ArrayList<>();
		Map<String,Integer> readProgressList = new HashMap<String,Integer>();
		// 共通本の表から必要情報を取り出す
		for(var bookShelf :bookShelfList) {
			BookView bookView = bookViewService.convertFromBookShelf(bookShelf);
			if(bookView != null ){
				bookViewList.add(bookView);//BookViewを追加
			}
			// 進捗度
			int bookId =  bookView.getBookId();
			int readProgress = plotDataService.getProgress(bookId);
			readProgressList.put(String.valueOf(bookId), readProgress);
		}
		// 登録本
		
//		System.out.println(commonBookList);
		model.addAttribute("bookViewList",bookViewList);
		model.addAttribute("readProgressList",readProgressList);
//		model.addAttribute("bookShelfList",bookShelfList);
//		model.addAttribute("commonBookList",commonBookList);
		
		// 登録メッセージ
		String registBookMessage = (String) session.getAttribute("registBookMessage");
		if(registBookMessage != null && registBookMessage.length()>0) {
			model.addAttribute("registBookMessage",registBookMessage);
			session.removeAttribute("registBookMessage");//消す
		}		
		return "top";
	}

	// 本の登録
	@GetMapping("registNewBook")
	public String registBookView(Model model) {
		System.out.println("@GetMapping(\"registNewBook\")");
		Account account = (Account) session.getAttribute("account");
		System.out.println(account);
		model.addAttribute("account",account);
		model.addAttribute("bookShelf",new BookShelf(0,account.getUserId(),null,0,null,0));
		// model.addAttribute("bookShelf",new BookShelf());
		return "registNewBook";
	}

	// ISBNコードから本の情報を取得
	@PostMapping("getCommonBook")
	public String getCommonBook(@Validated BookShelf bookShelf,
			BindingResult bindingResult,Model model) {
		System.out.println("@PostMapping(\"getCommonBook\")");
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);	
		System.out.println("userId:"+bookShelf.getUserId());
		System.out.println("commonBookId:"+bookShelf.getCommonBookId());
		model.addAttribute("bookShelf",bookShelf);
		if(bindingResult.hasErrors()) {	
			System.out.println("エラーあるよ");
			return "registNewBook";//リダイレクトはうまくいかない！
		}
		String commonBookId = bookShelf.getCommonBookId();//ISBNコード
		CommonBook commonBook = commonBookService.findById(commonBookId);// 共通本を取得
		if( commonBook != null ) {
			model.addAttribute("commonBook",commonBook);// 検索したIDの本の情報を反映
		}else {
			bindingResult.rejectValue("","","ISBNコードで見つかりませんでした");
		}
		return "registNewBook";
	}
	
//	本の登録
	@PostMapping("registNewBookOK")
	public String postRegistBookView(@Validated BookShelf bookShelf,
			BindingResult bindingResult,Model model) {
		System.out.println("@PostMapping(\"registNewBookOK\"");
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		model.addAttribute("bookShelf",bookShelf);
		if(bindingResult.hasErrors()) {	
			System.out.println("エラーあるよ");
			return "registNewBook";//リダイレクトはうまくいかない！
		}
		// エラーはない
		int userId = account.getUserId();
		String isbn = bookShelf.getCommonBookId();
		System.out.println(userId+","+isbn);
		boolean result = false ;
		try {
			result = bookShelfService.insert(userId,isbn);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(result != true) {
			// System.out.println("登録失敗しました！");
			bindingResult.rejectValue("","","登録失敗しました！値をいれなおしてください");
			return "registNewBook";
		}
		// 登録した本のメッセージ
		session.setAttribute("registBookMessage",commonBookService.findById(isbn).getTitle()+ "を登録しました");
		return "redirect:/top";
	}
	

	@GetMapping("showBook/{bookId}")
	public String showBookView(Model model,@PathVariable int bookId) {
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
//		System.out.println(account);
		int userId = account.getUserId();
		
		// 読了メッセージ
		String finishedMessage = (String) session.getAttribute("finishedMessage");
		if(finishedMessage != null && finishedMessage.length()>0) {
			model.addAttribute("finishedMessage",finishedMessage);
			session.removeAttribute("finishedMessage");//消す！
		}

			// model.addAttribute("finishedMessage","読了しました！");
		// 本の情報
		BookView bookView = bookViewService.findById(bookId);
		model.addAttribute("bookView",bookView);
//		System.out.println(bookView);
		
		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);
//		System.out.println("ownerId:"+ownerId);
		// 閲覧物のオーナー
		Owner owner = ownerService.getOwnerByUserId(ownerId);
		model.addAttribute("owner", owner);
		model.addAttribute("isOwner", (ownerId == userId));// 閲覧物はユーザのものか？
//		System.out.println(owner);
		
		// 次の読書プランへのリンクを表示つくるかどうか
		model.addAttribute("newReadPlan",readPlanService.checkNewReadPlan(bookId));
		
		// 読書プランの進捗を取得
		int readProgress = plotDataService.getProgress(bookId);
		model.addAttribute("readProgress",readProgress);
		
		return "showBook";
	}
	
	// 全ユーザーの本棚一覧
	@GetMapping("everyoneBook")
	public String everyoneBookView(Model model) {
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		// 本を所有しているユーザー取得
		List<Integer> userIdList = bookShelfService.getAllUserListHasBook();
		// 全ユーザの本棚
		List<BookShelfView> bookShelfViewList = new ArrayList<>();
		// 読書進捗
		Map<String,Integer> readProgressList = new HashMap<String,Integer>();
		
		int countMax = 10;
		for(Integer userId:userIdList) {
			// if(userId == account.getUserId()) continue;// 自分は表示しない
			List<BookShelf> bookShelfList = bookShelfService.findAllFromBookShelf(userId);
			if( bookShelfList.size() == 0 ) continue;// サイズ0は表示しない
			List<BookView> bookViewList = new ArrayList<>();
			Account accountElem = new Account(userId,userService.getUserName(userId),null,0,null);
			int count = 0;
			for(BookShelf bookShelf:bookShelfList) {
				BookView bookView = bookViewService.convertFromBookShelf(bookShelf);
				count++;
				if(count > countMax)continue;
				if(bookView != null ){
					bookViewList.add(bookView);//BookViewを追加
				}
				// 進捗度
				int bookId =  bookView.getBookId();
				int readProgress = plotDataService.getProgress(bookId);
				readProgressList.put(String.valueOf(bookId), readProgress);
			}
			int bookShelfSize = bookShelfList.size();
			// 本棚一覧用
			BookShelfView bookUser = new BookShelfView(accountElem,bookViewList,bookShelfSize);
			bookShelfViewList.add(bookUser);
		}
		model.addAttribute("bookShelfViewList",bookShelfViewList);
		model.addAttribute("readProgressList",readProgressList);
		return "everyoneBookShelf";
	}
	
	@ExceptionHandler(Exception.class)
    public String error(Exception e) {
        // エラー用のページに遷移させる
        return "failure";
    }
}
