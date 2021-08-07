package com.example.demo.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserSearchRequest implements Serializable{
	private int id;
	private String name;
	private int age;
}

/*
 * Serializableによりインスタンス化が出来ないというわけではない。
 * */
