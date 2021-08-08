package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;

@Mapper // mybatis-spring-boot-starterを導入
public interface UserMapper {
	
	User search(int id); // これが呼ばれたら、xmlのid="search"が呼ばれる(UserMapper.xmlの方に情報が記載)
	
	User search(UserSearchRequest userSearchRequest);
	
	List<User> searchAll();
	
	void add(UserSearchRequest user);
	
	void update(@Param("id") int id, @Param("name") String name, @Param("age") int age, @Param("birthday") LocalDate birthday);
	//@ParamはxmlのSQL文での変数名指定
	
	void delete(int id);
}
