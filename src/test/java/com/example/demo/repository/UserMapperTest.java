package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;

@ExtendWith(SpringExtension.class)
@MybatisTest
@TestPropertySource(locations = "classpath:test.properties")
class UserMapperTest {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;
	
	@Autowired
	private JdbcOperations jdbcOperations; 
	
	@Test
	void idを渡すとユーザー情報を一件返す() throws Exception {
		// 実行
		User user =  userMapper.search(1);
		
		// 検証
		assertThat(user.getId()).isEqualTo(1);
		assertThat(user.getName()).isEqualTo("崔云峰");
		assertThat(user.getAge()).isEqualTo(23);
		assertThat(user.getBirthday()).isEqualTo("1998-03-09");
	}
	
	@Test
	void ユーザー情報を全件返す() throws Exception {
		List<User> users =  userMapper.searchAll();
		
		assertThat(users.size()).isEqualTo(2);
		assertThat(users.get(1).getId()).isEqualTo(2);
		assertThat(users.get(1).getName()).isEqualTo("橋本裕太");
		assertThat(users.get(1).getAge()).isEqualTo(26);
		assertThat(users.get(1).getBirthday()).isEqualTo("1995-02-05");
	}
	
	@Test
	void ユーザー情報を一件新規登録する() throws Exception {
		// 準備
		UserSearchRequest user = new UserSearchRequest();
		user.setId(3); // AUTO_INCREMENT有 -> addメソッドの引数にid -> 設計ミス
		user.setName("野原しんのすけ");
		user.setAge(5);
		user.setBirthday(LocalDate.of(2016, 5 ,5));
		
		// 実行
		userMapper.add(user);
		User actualUser = 
				namedParameterJdbcOperations.queryForObject("SELECT * FROM users WHERE id=:id",
						new MapSqlParameterSource("id", 3),
						new BeanPropertyRowMapper<>(User.class));
		
		// 検証
		assertThat(actualUser.getId()).isEqualTo(3);
		assertThat(actualUser.getName()).isEqualTo("野原しんのすけ");
		assertThat(actualUser.getAge()).isEqualTo(5);
		assertThat(actualUser.getBirthday()).isEqualTo("2016-05-05");
	}
	
	@Test
	void ユーザー情報を一件更新する() throws Exception {
		userMapper.update(2, "陳鼎鼎", 27, LocalDate.of(1994, 8 ,26)); // 生年は未確定
		
		User actualUser = 
				namedParameterJdbcOperations.queryForObject("SELECT * FROM users WHERE id=:id",
						new MapSqlParameterSource("id", 2),
						new BeanPropertyRowMapper<>(User.class));
		
		assertThat(actualUser.getId()).isEqualTo(2);
		assertThat(actualUser.getName()).isEqualTo("陳鼎鼎");
		assertThat(actualUser.getAge()).isEqualTo(27);
		assertThat(actualUser.getBirthday()).isEqualTo("1994-08-26");
	}
	
	@Test
	void ユーザー情報を一件削除する() throws Exception {
		userMapper.delete(1);

		List<User> actualUsers = jdbcOperations.query("SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
		
		assertThat(actualUsers.size()).isEqualTo(1);
		assertThat(actualUsers.get(0).getId()).isEqualTo(2);
		assertThat(actualUsers.get(0).getName()).isEqualTo("橋本裕太");
		assertThat(actualUsers.get(0).getAge()).isEqualTo(26);
		assertThat(actualUsers.get(0).getBirthday()).isEqualTo("1995-02-05");
	}

}
