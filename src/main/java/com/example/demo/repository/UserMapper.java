package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;

@Mapper // mybatis-spring-boot-starterを導入
public interface UserMapper {
	
	User search(UserSearchRequest user); // これが呼ばれたら、xmlのid="search"が呼ばれる(UserMapper.xmlの方に情報が記載)
	
	List<User> searchAll();
	
	void add(UserSearchRequest user);
	
	void delete(int id);
}
