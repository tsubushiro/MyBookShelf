package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.BookView;
import com.example.demo.entity.MemoComment;
import com.example.demo.entity.Owner;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.BookViewService;
import com.example.demo.service.CommonBookService;
import com.example.demo.service.MemoCommentService;
import com.example.demo.service.OwnerService;
import com.example.demo.service.PlotDataService;
import com.example.demo.service.ReadPlanService;
import com.example.demo.service.UserService;
import com.example.demo.validator.UnuseableBookValidator;

import jakarta.servlet.http.HttpSession;

// メモ処理
@Controller
public class MemoCommentController {
	
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
	MemoCommentService memoCommentService;
	
	@Autowired
	UserService userService;
	
	// インスタンスをモデルに格納
	@ModelAttribute("account")
	public Account setUpAccount() {
		return new Account();
	}
	// インスタンスをモデルに格納
	@ModelAttribute("bookShelf")
	public BookShelf setBookShelf() {
		return new BookShelf();
	}
	
	@Autowired
	private HttpSession session;
	
	// メモの投稿・更新
	@PostMapping("updateMemoComment")
	public String postUpdateMemoComment(@Validated MemoComment memoComment,
			BindingResult bindingResult,Model model) {
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();	
		// メモ
		int memoCommentId = memoComment.getMemoCommentId();
		int bookId = memoComment.getBookId();
		String title = memoComment.getTitle();
		String text = memoComment.getText();

		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);	
		if(ownerId!=userId) { // 所有者かどうかをチェックして違ってたらfailへ
			return "failure";
		}
		if(bindingResult.hasErrors()) {	
			System.out.println("エラーあるよ");
			// 本の情報
			BookView bookView = bookViewService.findById(bookId);
			model.addAttribute("bookView",bookView);
			// 閲覧物のオーナー
			Owner owner = ownerService.getOwnerByUserId(ownerId);
			model.addAttribute("owner", owner);
			model.addAttribute("isOwner", (ownerId == userId));// 閲覧物はユーザのものか？
			model.addAttribute("memoComment", memoComment);
			return "showMemo";//リダイレクトはうまくいかない！
		}
		// System.out.println();
		MemoComment memoCommentNew = new MemoComment(
				memoCommentId,
				bookId,
				title,
				text,
				null
				);
		boolean result = false;
		// メモ内容の更新か登録
		if(memoCommentId > 0) {
			result = memoCommentService.update(memoCommentNew);// 更新
		}else {
			result = memoCommentService.insert(memoCommentNew);// 登録
			memoCommentId = memoCommentService.findLastIdByBookId(bookId);// 最新のものを取得 
		}
		if(result==false) return "failure";// 失敗へ
		// System.out.println(memoCommentNew);
		return "redirect:/activeMemoComment/"+bookId+"?memoCommentId="+memoCommentId;
	}

	// 新規メモ作成
	@GetMapping("createMemoComment/{bookId}")
	public String createMemoCommentView(Model model,
			@PathVariable int bookId) {
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();	
//		// メモ
//		int memoCommentId = memoComment.getMemoCommentId();
//		int bookId = memoComment.getBookId();
//		String title = memoComment.getTitle();
//		String text = memoComment.getText();

		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);	
		if(ownerId!=userId) { // 所有者かどうかをチェックして違ってたらfailへ
			return "failure";
		}
		// System.out.println();
		MemoComment memoCommentNew = new MemoComment(
				0,
				bookId,
				"新規メモ",
				"",
				null
				);
		boolean result = false;
		// メモ内容の更新か登録
		result = memoCommentService.insert(memoCommentNew);// 登録
		if(result==false) return "failure";// 失敗へ
		int memoCommentId = memoCommentService.findLastIdByBookId(bookId);// 最新のものを取得
//		System.out.println(memoCommentNew);
		return "redirect:/activeMemoComment/"+bookId+"?memoCommentId="+memoCommentId;			
	}
	
	// 新規メモ POST側（つかわないかも）
	@PostMapping("createMemoComment")
	public String postcreateMemoComment(MemoComment memoComment,
			Model model) {
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();	
//		// メモ
//		int memoCommentId = memoComment.getMemoCommentId();
		int bookId = memoComment.getBookId();
//		String title = memoComment.getTitle();
//		String text = memoComment.getText();

		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);	
		if(ownerId!=userId) { // 所有者かどうかをチェックして違ってたらfailへ
			return "failure";
		}
		// System.out.println();
		MemoComment memoCommentNew = new MemoComment(
				0,
				bookId,
				"新規メモ",
				"",
				null
				);
		boolean result = false;
		// メモ内容の更新か登録
		result = memoCommentService.insert(memoCommentNew);// 登録
		if(result==false) return "failure";// 失敗へ
		int memoCommentId = memoCommentService.findLastIdByBookId(bookId);// 最新のものを取得
//		System.out.println(memoCommentNew);
		return "redirect:/activeMemoComment/"+bookId+"?memoCommentId="+memoCommentId;
	}

	
	// メモ削除
	@PostMapping("deleteMemoComment")
	public String postDeleteMemoComment(MemoComment memoComment,
			Model model) {
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();
		// メモ
		int memoCommentId = memoComment.getMemoCommentId();
		int bookId = memoComment.getBookId();
		// メモのIDが0だったら、指定されたmemoCommentは削除対象ではないので削除処理しない
		if( memoCommentId <= 0 ) return "redirect:/memoComment/"+bookId;
//		String title = memoComment.getTitle();
//		String text = memoComment.getText();

		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);	
		if(ownerId!=userId) { // 所有者かどうかをチェックして違ってたらfailへ
			return "failure";
		}
		// 削除
		boolean result = memoCommentService.removeById(memoCommentId);
		if(result==false) return "failure";// 失敗へ
		// 削除されたら最新のもの
		int lastMemoCommentId = memoCommentService.findLastIdByBookId(bookId);
		if( lastMemoCommentId > 0 ) session.setAttribute("activeMemoCommentId", lastMemoCommentId);
		// メモ内容の更新か登録
		return "redirect:/memoComment/"+bookId;
	}
	
	// メモのアクティブ化
	@GetMapping("activeMemoComment/{bookId}")
	public String memoCommentView(Model model,
			@PathVariable int bookId,
			@RequestParam int memoCommentId) {
		session.setAttribute("activeMemoCommentId", memoCommentId);
		// System.out.println(memoCommentId+"がアクティブ");
		return "redirect:/memoComment/"+bookId;
	}
	
	// メモの表示
	@GetMapping("memoComment/{bookId}")
	public String memoCommentView(Model model,
			@PathVariable int bookId/*,
			@RequestParam int memoCommentId*/) {
//		System.out.println(memoCommentId);
		// アカウント
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account", account);
		int userId = account.getUserId();
		
		// 本の情報
		BookView bookView = bookViewService.findById(bookId);
		model.addAttribute("bookView",bookView);
		
		// 閲覧している本のオーナー
		int ownerId = ownerService.getBookShelfOwnerById(bookId);
		
		// 閲覧物のオーナー
		Owner owner = ownerService.getOwnerByUserId(ownerId);
		model.addAttribute("owner", owner);
		model.addAttribute("isOwner", (ownerId == userId));// 閲覧物はユーザのものか？

//		System.out.println(session.getAttribute("activeMemoCommentId"));
		// アクティブなメモのIDを取得
		int activeMemoCommentId = session.getAttribute("activeMemoCommentId") != null ? 
				(int) session.getAttribute("activeMemoCommentId") : 0;
//		System.out.println(activeMemoCommentId+"がアクティブ");
		model.addAttribute("ctiveMemoCommentId", activeMemoCommentId);//アクティブ表示のため
		
		// メモの取得
		List<MemoComment> memoCommentList = bookView.getAdditionalInfo().getMemoCommentList();
//		System.out.println(memoCommentList);
		
		// 表示するメモ
		MemoComment memoComment = new MemoComment(
				0,
				bookId,
				"新規メモ",
				"",
				LocalDateTime.now()
				);
		
		// メモのリストがみつかった場合の処理
		if(memoCommentList != null && memoCommentList.size() > 0) {
			// メモ
			memoComment = memoCommentList.get(0);//一つ目
			
			// アクティブなメモのIDが0以上だったらちゃんと探す
			if( activeMemoCommentId > 0) {
				// メモの取得
				for(MemoComment memoCommentElem:memoCommentList) {
					if(memoCommentElem.getMemoCommentId() == activeMemoCommentId) {
						memoComment = memoCommentElem; // 内容のコピー
						break;
					}
				}
			}else {
				// とりあえず一つ目
				activeMemoCommentId = memoComment.getMemoCommentId();
			}
		}
//		System.out.println(activeMemoCommentId+"がアクティブ");
		model.addAttribute("activeMemoCommentId", activeMemoCommentId);//アクティブなメモ
		model.addAttribute("memoComment", memoComment);//表示するメモ
		return "showMemo";
	}
	
	@ExceptionHandler(Exception.class)
    public String error(Exception e) {
        // エラー用のページに遷移させる
        return "failure";
    }
}
