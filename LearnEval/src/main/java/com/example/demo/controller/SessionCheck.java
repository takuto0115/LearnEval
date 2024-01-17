package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

public class SessionCheck {

	public boolean sessionCheck(HttpSession session) {
		// TODO 自動生成されたコンストラクター・スタブ
		if(session.getAttribute("name")==null) {
			return true;
		}
		return false;
	}
}
