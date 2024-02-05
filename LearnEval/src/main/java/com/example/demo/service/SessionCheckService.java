package com.example.demo.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionCheckService {

	//教員用セッションチェック
	public boolean teacherSessionCheck(HttpSession session) {
		if(session.getAttribute("teacherID") == null){
			return false;
		}
		return true;
	}

	//生徒用セッションチェック
	public boolean studentSessionCheck() {
		return true;
	}

}
