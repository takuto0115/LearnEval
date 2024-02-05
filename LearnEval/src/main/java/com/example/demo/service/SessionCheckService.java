package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class SessionCheckService {

	//教員用セッションチェック
	public boolean teacherSessionCheck() {
		return true;
	}
	
	//生徒用セッションチェック
	public boolean studentSessionCheck() {
		return true;
	}
}
