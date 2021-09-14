package com.example.demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserControlerクラスをテスト")
class UserControllerTest {
	
	private MockitoSession session;
	private MockMvc mockMvc;
	
	@Mock
	private UserService userService;
	
	@InjectMocks
	private UserController target;
	
	/* 参考:https://qiita.com/Tsuyoshi_Murakami/items/2fa5c0a41432f705400a#%E3%83%A2%E3%83%83%E3%82%AF%E3%82%AF%E3%83%A9
	        %E3%82%B9%E3%81%AB%E5%AF%BE%E8%B1%A1%E3%82%AF%E3%83%A9%E3%82%B9%E3%81%AE%E3%82%A2%E3%83%8E%E3%83%86%E3%83%BC
	        %E3%82%B7%E3%83%A7%E3%83%B3%E3%81%8C%E4%BB%98%E4%B8%8E%E3%81%95%E3%82%8C%E3%82%8B%E3%82%88%E3%81%86%E3%81%AB
	        %E3%81%AA%E3%81%A3%E3%81%9F
	*/
	@BeforeEach
	void setUp() {
		// MockitoAnnotations.initMocks(this)の新しいバージョン(モックオブジェクトの初期化)
	    session = Mockito.mockitoSession().initMocks(this).startMocking();
	    this.mockMvc = MockMvcBuilders.standaloneSetup(target).build();
	}
	
	@AfterEach
	void tearDown() {
	    session.finishMocking();
	}
	
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
		@DisplayName("UserServiceクラスのsearchAllメソッド一回呼び出す")
		void UserServiceクラスのsearchAllメソッド一回呼び出す() throws Exception {
			// Arrange
			List<User> list = new ArrayList<User>();
			doReturn(list).when(userService).searchAll();
			
			// Act & Assert
			mockMvc.perform(get("/"))
				.andExpect(status().isOk());
			verify(userService, times(1)).searchAll();
		}
		
		@Test
		@DisplayName("modelに変数usersinfoでList/User型のデータを流す")
		void modelに変数usersinfoでList_User型のデータを流す() throws Exception {
			// Arrange
			List<User> list = new ArrayList<User>();
			doReturn(list).when(userService).searchAll();
			
			// Act & Assert
			mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("usersinfo", list));
		}
	}
	
	// ここから
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
				.flashAttr("userSearchRequest", userSearchRequest)
			).andExpect(status().isOk())
			.andExpect(model().attribute("userinfo", userService.search(userSearchRequest)))
			.andExpect(model().hasNoErrors());
		}
	}
	
	@Nested
	class ユーザー登録画面を表示 {
		@Test
		@DisplayName("user/create.htmlを返す")
		void user_create_htmlを返す() throws Exception {
			mockMvc.perform(get("/user/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("user/create"));
		}
	}
	
	@Nested
	class ユーザー登録を行い_ホームにリダイレクトする {
		
		@Nested
		class 入力エラー時_user_create_htmlを返す {
			@Test
			@DisplayName("UserSearchRequestが空のとき、user/create.htmlを返す")
			void user_create_htmlを返す() throws Exception {
				
				mockMvc.perform(
					post("/user/create")
					.param("id", "")
					.param("name", "")
					.param("age", "")
					.param("birthday", "")
				).andExpect(status().isOk())
				.andExpect(view().name("user/create"))
				.andExpect(model().attributeHasErrors("userSearchRequest"));
			}
		}
		
		@Test
		@DisplayName(" 正常時、/にリダイレクトする")
		void  正常時にリダイレクトする() throws Exception {
			// 前処理
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			userSearchRequest.setId(25);
			userSearchRequest.setName("佐々木伸二");
			userSearchRequest.setAge(29);
			userSearchRequest.setBirthday(LocalDate.of(1991,10,29));
			
			mockMvc.perform(
				post("/user/create")
				.flashAttr("userSearchRequest", userSearchRequest)
			).andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		}
		
	}
}
