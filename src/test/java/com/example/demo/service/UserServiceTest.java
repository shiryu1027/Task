package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dbunitdemo.CsvDataSetLoader;
import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@SpringBootTest
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class, // DIを使えるように指定
        TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@Transactional // @DatabaseSetupで投入するデータをテスト処理と同じトランザクション制御とする
@TestPropertySource(locations = "classpath:test.properties")
class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;
	
	@Autowired
	private JdbcOperations jdbcOperations; 

	@Test
	@DatabaseSetup("/testdata/init-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	void searchメソッドにおいてidを指定すると_一件のユーザー情報を返す() {
		User expected = new User();
		expected.setId(2);
		expected.setName("Otter");
		expected.setAge(23);
		expected.setBirthday(LocalDate.of(1998, 3, 9));
		User actual = userService.search(2);
		assertThat(actual).isEqualTo(expected);
  }


	@Test
	@DatabaseSetup("/testdata/init-data")
	@ExpectedDatabase(value = "/testdata/init-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	void searchAllメソッドを実行すると_DBにあるすべてのユーザー情報を返す() {
		User expected = new User();
		expected.setId(2);
		expected.setName("Otter");
		expected.setAge(23);
		expected.setBirthday(LocalDate.of(1998, 3, 9));
		
		List<User> actual = userService.searchAll();
		assertThat(actual.size()).isEqualTo(5);
		assertThat(actual.get(1)).isEqualTo(expected); // 代表値をチェック
	}

	@Test
	@DatabaseSetup("/testdata/init-data")
	@ExpectedDatabase(value = "/testdata/after-create-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	void createメソッドによって_ユーザー情報を新規登録する() {
		UserSearchRequest expected = new UserSearchRequest();
		expected.setId(6);
		expected.setName("橋本裕太");
		expected.setAge(26);
		expected.setBirthday(LocalDate.of(1995, 2, 5));
		
		userService.create(expected);
		User actual = 
				namedParameterJdbcOperations.queryForObject("SELECT * FROM users WHERE id=:id",
						new MapSqlParameterSource("id", 6),
						new BeanPropertyRowMapper<>(User.class));
		
		assertThat(actual.getId()).isEqualTo(expected.getId());
		assertThat(actual.getName()).isEqualTo(expected.getName());
		assertThat(actual.getAge()).isEqualTo(expected.getAge());
		assertThat(actual.getBirthday()).isEqualTo(expected.getBirthday());
	}

	@Test
	@DatabaseSetup("/testdata/init-data")
	@ExpectedDatabase(value = "/testdata/after-update-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	void updateメソッドによって_情報が更新される() {
		userService.update(5, "十七君", 33, LocalDate.of(1989,11,01));
		
		User actual = 
				namedParameterJdbcOperations.queryForObject("SELECT * FROM users WHERE id=:id",
						new MapSqlParameterSource("id", 5),
						new BeanPropertyRowMapper<>(User.class));
		
		assertThat(actual.getId()).isEqualTo(5);
		assertThat(actual.getName()).isEqualTo("十七君");
		assertThat(actual.getAge()).isEqualTo(33);
		assertThat(actual.getBirthday()).isEqualTo("1989-11-01");
	}

	@Test
	@DatabaseSetup("/testdata/init-data")
	@ExpectedDatabase(value = "/testdata/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	void deleteメソッドによって_情報が削除される() {
		userService.delete(4);
		
		List<User> actual = jdbcOperations.query("SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
		
		assertThat(actual.size()).isEqualTo(4);
		assertThat(actual.get(2).getName()).isEqualTo("Ding");
		assertThat(actual.get(3).getName()).isEqualTo("Mr.17");
	}
  
}
