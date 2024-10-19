package com.example.demo.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SessionScope
public class PasswordEdit {
	private String oldPass;
	
	@Pattern(regexp="[a-zA-Z0-9]*",message="パスワードは半角英数字で入力してください")
	@Length(min=3,max=10,message="パスワードは3文字以上10文字以内で入力してください")
	private String newPass;
	
	private String newPass2;
}
