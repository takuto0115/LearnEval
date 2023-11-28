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
	
	@RequestMapping(path = "/teachermain", method = RequestMethod.POST)
	public String mainPost(String page) {
		switch(page) {
		case"テスト一覧":
			return "redirect:/teachertestmenu";
		case"個人成績一覧":
			return "redirect:/teacherstumenu";
		case"メッセージ一覧":
			return "redirect:/teachermessage";

		}
		return "/studentmain";
	}
	
	@RequestMapping(path = "/teachertestmenu", method = RequestMethod.GET)
	public String testMenuGet() {
		return "teachertestmenu";
	}
	

	@RequestMapping(path = "/teacherstumenu", method = RequestMethod.GET)
	public String stuMenuGet() {
		return "teacherstumenu";
	}
	
	@RequestMapping(path = "/testexamedit", method = RequestMethod.GET)
	public String testExamGet() {
		return "testexamedit";
	}
	
	
	@RequestMapping(path = "/teachermessage", method = RequestMethod.GET)
	public String mesGet() {
		return "teachermessage";
	}
}
