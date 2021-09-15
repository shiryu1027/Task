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
	
	// READ処理
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
				.andExpect(model().attribute("usersinfo", list))
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
		@DisplayName("UserServiceクラスのsearchメソッド一回呼び出す")
		void UserServiceクラスのsearchメソッド一回呼び出す() throws Exception {
			// Arrange
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			User user = new User();
			doReturn(user).when(userService).search(userSearchRequest);
			
			// Act & Assert
			mockMvc.perform(
					post("/user/id_search"))
				.andExpect(status().isOk());
			verify(userService, times(1)).search(userSearchRequest);
		}
		
		@Test
		@DisplayName("modelに変数userinfoでUser型のデータを流す")
		void modelに変数userinfoでUser型のデータを流す() throws Exception {
			
			// 前処理
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			User user = new User();
			doReturn(user).when(userService).search(userSearchRequest);
			
			// 実行&検証
			mockMvc.perform(
				post("/user/id_search")
			).andExpect(status().isOk())
			.andExpect(model().attribute("userinfo", userService.search(userSearchRequest)))
			.andExpect(model().hasNoErrors());
		}
	}
	
	// CREATE処理
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
		
		@Test
		@DisplayName("入力エラー時_user_create_htmlを返す")
		void 入力エラー時_user_create_htmlを返す() throws Exception {
			
			// Arrange
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			userSearchRequest.setId(-1);
			
			// Act & Assert
			mockMvc.perform(
				post("/user/create")
				.flashAttr("userSearchRequest", userSearchRequest)
			).andExpect(status().isOk())
			.andExpect(view().name("user/create"));
		}
		
		@Test
		@DisplayName(" 正常時、ユーザー登録のサービスメソッドが実行できているか")
		void  正常時にユーザー登録のサービスメソッドが実行できているか() throws Exception {
			// 前処理
			UserSearchRequest userSearchRequest = new UserSearchRequest();
			userSearchRequest.setId(25);
			userSearchRequest.setName("佐々木伸二");
			userSearchRequest.setAge(29);
			userSearchRequest.setBirthday(LocalDate.of(1991,10,29));
			// createはvoid型メソッドなので、戻り値はdoNothing()を用いる。
			doNothing().when(userService).create(userSearchRequest);
			
			mockMvc.perform(
				post("/user/create")
				.flashAttr("userSearchRequest", userSearchRequest));
			
			verify(userService, times(1)).create(userSearchRequest);
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
			doNothing().when(userService).create(userSearchRequest);
			
			mockMvc.perform(
				post("/user/create")
				.flashAttr("userSearchRequest", userSearchRequest)
			).andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		}
	}
	
	@Nested
	class ユーザー情報更新画面を取得_また既存データを流す {
		
		@Test
		@DisplayName("user/update.htmlを返す")
		void user_search_htmlを返す() throws Exception {
			User user = new User();
			int id = 1;
			doReturn(user).when(userService).search(id);
			
			mockMvc.perform(
					get("/user/update/id=1"))
			.andExpect(status().isOk())
			.andExpect(view().name("user/update"));
		}
		
		@Test
		@DisplayName(" ユーザー情報取得のサービスメソッドが呼ばれている")
		void  ユーザー情報取得のサービスメソッドが呼ばれている() throws Exception {
			
			User user = new User();
			int id = 1;
			doReturn(user).when(userService).search(id);
			
			mockMvc.perform(
				get("/user/update/id=1"))
				.andExpect(status().isOk());
			
			verify(userService, times(1)).search(1);
		}
		
		@Test
		@DisplayName("modelに変数userinfoでUser型のデータを流す")
		void modelに変数userinfoでUser型のデータを流す() throws Exception {
			
			// 前処理
			User user = new User();
			int id = 1;
			doReturn(user).when(userService).search(id);
			
			// 実行&検証
			mockMvc.perform(
				get("/user/update/id=1")
			).andExpect(status().isOk())
			.andExpect(model().attribute("userinfo", userService.search(id)))
			.andExpect(model().hasNoErrors());
		}
	}
	
	@Nested
	class ユーザー更新を行い_ホームにリダイレクトする {
		
		@Test
		@DisplayName("ユーザー情報更新のサービスメソッドが実行できているか")
		void  正常時にユーザー登録のサービスメソッドが実行できているか() throws Exception {
			// 前処理
			UserSearchRequest user = new UserSearchRequest();
			user.setId(25);
			user.setName("佐々木伸二");
			user.setAge(29);
			user.setBirthday(LocalDate.of(1991,10,29));
			doNothing().when(userService).update(user.getId(), user.getName(), user.getAge(), user.getBirthday());
			
			mockMvc.perform(
				post("/user/update/id=25")
				.flashAttr("userSearchRequest", user))
				.andExpect(status().isFound());
			
			verify(userService, times(1)).update(user.getId(), user.getName(), user.getAge(), user.getBirthday());
		}
			
		@Test
		@DisplayName(" 正常時、/にリダイレクトする")
		void  正常時にリダイレクトする() throws Exception {
			// 前処理
			UserSearchRequest user = new UserSearchRequest();
			user.setId(25);
			user.setName("佐々木伸二");
			user.setAge(29);
			user.setBirthday(LocalDate.of(1991,10,29));
			doNothing().when(userService).update(user.getId(), user.getName(), user.getAge(), user.getBirthday());
			
			mockMvc.perform(
				post("/user/update/id=25")
				.flashAttr("userSearchRequest", user)
			).andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		}
	}
	
	@Nested
	class ユーザー情報の削除を行いホームにリダイレクトする {
		
		@Test
		@DisplayName("ユーザー情報削除のサービスメソッドが実行できているか")
		void  ユーザー情報削除のサービスメソッドが実行できているか() throws Exception {
			// 前処理
			int id = 25;
			doNothing().when(userService).delete(id);
			
			mockMvc.perform(
				post("/user/delete/id=25"))
				.andExpect(status().isFound());
			
			verify(userService, times(1)).delete(id);
		}
			
		@Test
		@DisplayName(" 削除後、/にリダイレクトする")
		void  削除後リダイレクトする() throws Exception {
			// 前処理
			int id = 25;
			doNothing().when(userService).delete(id);
			
			mockMvc.perform(
				post("/user/delete/id=25")
			).andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		}
	}
}
