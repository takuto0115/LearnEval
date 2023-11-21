package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TeacherController {
	
	//コピペ用サンプル(ページ表示用メソッド)
	@RequestMapping(path = "/teachermain", method = RequestMethod.GET)
	public String mainGet() {
		return "teachermain";
	}
}
