package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserControlerクラスをテスト")
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	UserService userService;
	
	@Nested
	class 全ユーザー情報を表示したトップページを返す{
		@Test
		@DisplayName("index.htmlを返す")
		void index_htmlを返す() throws Exception {
			mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
		}
		
		@Test
		@DisplayName("modelに変数listでList/User型のデータを流す")
		void modelに変数listでList_User型のデータを流す() throws Exception {
			mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("usersinfo", userService.searchAll()))
				.andExpect(model().hasNoErrors());
		}
	}
	
	@Nested
	class ユーザー情報id検索画面を返す {
		@Test
		@DisplayName("user/search.htmlを返す")
		void user_search_htmlを返す() throws Exception {
			mockMvc.perform(get("/user/search"))
			.andExpect(status().isOk())
			.andExpect(view().name("user/search"));
		}
	}
	
	@Nested
	class ユーザー情報id検索画面に検索結果を返す{
		
		@Test
		@DisplayName("user/search.htmlを返す")
		void user_search_htmlを返す() throws Exception {
			mockMvc.perform(
					post("/user/id_search"))
			.andExpect(status().isOk())
			.andExpect(view().name("user/search"));
		}
		
		@Test
		@DisplayName("modelに変数userinfoでUser型のデータを流す")
		void modelに変数userinfoでUser型のデータを流す() throws Exception {
			
			// 前処理
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			userSearchRequest.setId(1);
			
			// 実行&検証
			mockMvc.perform(
				post("/user/id_search")
				.flashAttr("userSearchRequest", userSearchRequest))
			.andExpect(status().isOk())
			.andExpect(model().attribute("userinfo", userService.search(userSearchRequest)))
			.andExpect(model().hasNoErrors());
		}
	}
}
