package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserSearchRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserMapper;

/*
 * サービスクラスを宣言
 * @ServiceはBeanとしてマークする=DIコンテナで管理されるオブジェクトであることを宣言
 */
@Service 
public class UserService {
	
	@Autowired
	private UserMapper userMapper;

	//searchの引数にid(userSearchRequestが内包しているid)が入ってくる
	public User search(UserSearchRequest userSearchRequest) {
		return userMapper.search(userSearchRequest); // userMapperのサーチメソッドを呼び出す
	}

	public User search(int id) { // 1種類にまとめる必要あり
		return userMapper.search(id);
	}
	
	public List<User> searchAll() {
		return userMapper.searchAll();
	}
	
	public void create(UserSearchRequest userSearchRequest) {
		userMapper.add(userSearchRequest);;
	}
	
	public void update(int id, String name, int age) {
		userMapper.update(id, name, age);
	}
	
	public void delete(int id) {
		userMapper.delete(id);
	}
}
