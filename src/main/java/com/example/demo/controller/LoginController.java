package com.example.demo.controller;

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
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Account;
import com.example.demo.entity.BookShelf;
import com.example.demo.entity.LoginUser;
import com.example.demo.entity.ReadPlanView;
import com.example.demo.service.BookShelfService;
import com.example.demo.service.CommonBookService;
import com.example.demo.service.UserService;
import com.example.demo.validator.UnuseableNameValidator;

import jakarta.servlet.http.HttpSession;

// アカウント処理
@Controller
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookShelfService bookShelfService;
	
	@Autowired
	CommonBookService commonBookService;
	
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
	// インスタンスをモデルに格納
	@ModelAttribute("readPlan")
	public ReadPlanView setReadPlanView() {
//		System.out.println("よばれた");
		return new ReadPlanView();
	}
	
	//
    @Autowired
    UnuseableNameValidator validator;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }
	
	@GetMapping("show")
	public String loginView() {
//		session.invalidate();//(存在していれば)セッション内容を消す
		// 途中でおかしくなるので一旦コメントアウト
		return "index";
	}
	
	// しばらくしたら消す！
//	@PostMapping("show")//登録画面(regist)から遷移してくる
//	public String postShowView(@Validated Account account,
//				BindingResult bindingResult,Model model) {
//		System.out.println("※※これは呼ばれたらだめ※※");
//		return "failure";
//	}
	
	@GetMapping("register")
	public String getRegisterView(Model model) {
	// 上の public Account setUpaccount()がない場合は引数を渡す
	// public String getRegisterView(Account account) { //なぜかaccountを引数にしてわたす
		model.addAttribute("account",new Account());
		return "registUser";
	}
	
	@PostMapping("registOK")
	public String postRegistOKView(@Validated Account account,
			BindingResult bindingResult,Model model) {
		if(bindingResult.hasErrors()) {
			System.out.println("エラーあるよ");
			return "registUser";//リダイレクトはうまくいかない！
		}
		// アカウントの登録
		boolean result = userService.createNewAccount(account);
		// System.out.println(result);
		// アカウントの情報をそのままいれる
		if(result) {
			account = userService.loginCheck(new LoginUser(account.getName(),account.getPass()));
			session.setAttribute("account",account);
			model.addAttribute("account",account);
			return "redirect:/top";
		}
		return "registUser";
	}
	@PostMapping("login")
	public String loginResultView(LoginUser loginUser ,Model model) {
		Account account = userService.loginCheck(loginUser);
//		System.out.println(account);
		if(account == null) {
//			return "index";
			return "failure";
		}
		session.setAttribute("account", account);
		return "redirect:/top";
	}
	
	@GetMapping("logout")
	public String logoutView(Model model) {
		Account account = (Account) session.getAttribute("account");
		if(account == null) {
			return "redirect:/show";//リダイレクトの場合はマッピング名
		}
//		model.addAttribute("account",session.getAttribute("account"));
//		account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		return "logout";
	}
	@GetMapping("remove")
	public String removeView(Model model) {
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		return "removeUser";
	}
	@GetMapping("removeOK")
	public String removeOKView() {
		Account account = (Account) session.getAttribute("account");
		boolean result = userService.removeAccount(account);
		if(result == true) {
			// System.out.println("削除成功！");
		}
		return "redirect:/show";
	}
	@GetMapping("loginOK")
	public String loginOKView(Model model) {
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		return "loginOK";
	}
	@GetMapping("userInfo")
	public String userInfoView(Model model) {
		Account account = (Account) session.getAttribute("account");
		model.addAttribute("account",account);
		return "userInfo";
	}
	@GetMapping("editUser")
	public String editUserView(Model model) {
		Account account = (Account) session.getAttribute("account");
//		System.out.println(account);
//		System.out.println(account.getPass());
		model.addAttribute("account",account);
		return "editUser";
	}
	@PostMapping("editOK")
	public String postEditOKView(@Validated Account account,
			BindingResult bindingResult,Model model) {
		if(bindingResult.hasErrors()) {
			System.out.println("エラーあるよ");
			return "editUser";//リダイレクトはうまくいかない！
		}
		
		// TO アカウントをとってきて、セッションにためる
		// サービスつくる
		System.out.println("ここまで！");
		Account result = userService.updateAccount(account);
		System.out.println("result:"+result);
		// トップに遷移させる場合はaccountのデータを取得して
		// sessionに入れる(アカウント情報が変更されているため）
		if(result != null) {
			session.setAttribute("account", result);
			model.addAttribute("account",result);
			return "userInfo";
		}
		return "editUser";
	}
	
	@ExceptionHandler(Exception.class)
    public String error(Exception e) {
        // エラー用のページに遷移させる
        return "failure";
    }
}
