package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.service.UserService;
import com.example.demo.entity.User;

@Controller
public class UserController {
	
	// UserService型のインスタンス作成
	@Autowired
	UserService userService; 
	
	// ホームぺージ取得
	@GetMapping("/")
	public String displaySearchAll(Model model) {
		List<User> list = userService.searchAll();
		model.addAttribute("usersinfo", list);
		return "/index";
	}
	
	// ユーザー情報検索画面を表示
	@GetMapping("/user/search")
	public String displaySearch() {
		return "user/search";
	}
	
	// ユーザー情報検索画面に検索結果を表示
	@PostMapping("/user/id_search")
	public String search(@ModelAttribute UserSearchRequest userSearchRequest, Model model) { // @ModelAttributeにより、自動インスタンス化
		User user = userService.search(userSearchRequest); // userSearchRequestはpostされてきたidデータを運ぶための段ボールのようなもの
		model.addAttribute("userinfo", user); // "userinfo"は変数名、userはオブジェクト
		return "user/search";
	}
	
	@GetMapping("/user/create")
	public String create() {
		return "user/create";
	}
	
	@PostMapping("/user/create")
	public String createOne(@Validated @ModelAttribute UserSearchRequest userSearchRequest, BindingResult result) {
		if(result.hasErrors()) {
			return "user/create";
		}
		userService.create(userSearchRequest);
		return "redirect:/"; //GetMappingを探している
	}
	
	@GetMapping("/user/update/id={id}")
	public String update(@PathVariable int id, Model model) {
		model.addAttribute("userinfo", id);
		return "/user/update";
	}
	
	@PostMapping("/user/update/id={id}")
	public String updateProcess() {
		return "redirect:/";
	}
}