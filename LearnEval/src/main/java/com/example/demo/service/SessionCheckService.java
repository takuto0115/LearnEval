package com.example.demo.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionCheckService {

	//教員用セッションチェック
	public boolean teacherSessionCheck(HttpSession session) {
		if(session.getAttribute("teacherID") == null){
			System.out.println(session.getAttribute("teacherID"));
			return true;
		}
		return false;
	}

	//生徒用セッションチェック
	public boolean studentSessionCheck(HttpSession session) {
		if (session.getAttribute("studentID") == null) {
			return true;
		}
		return false;
	}

}
