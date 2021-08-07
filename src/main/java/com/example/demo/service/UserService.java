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
	/*
	 * 匿名クラスである
	 * したがって、上記の@Autowiredは
	 * = new UserMapper() {
	 * } である
	 * オーバーライドはしていない？？？
	 * もしくは自動でオーバーライドされている？？？
	 */
	
	//searchの引数にid(userSearchRequestが内包しているid)が入ってくる
	public User search(UserSearchRequest userSearchRequest) {
		return userMapper.search(userSearchRequest); // userMapperのサーチメソッドを呼び出す
	}
	
	public List<User> searchAll() {
		return userMapper.searchAll();
	}
	
	public void create(UserSearchRequest userSearchRequest) {
		userMapper.add(userSearchRequest);;
	}
}
