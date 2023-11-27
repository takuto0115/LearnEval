package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StudentController {
	
	@RequestMapping(path = "/studentmain", method = RequestMethod.GET)
	public String mainGet() {
		return "studentmain";
	}
	
	@RequestMapping(path = "/studentmain", method = RequestMethod.POST)
	public String mainPost(String page) {
		switch(page) {
		case"テスト一覧":
			return "redirect:/studenttestmenu";
		case"成績一覧":
			return "redirect:/studenteval";
		case"メッセージ一覧":
			return "redirect:/studentmessage";

		}
		return "/studentmain";
	}
	
	@RequestMapping(path = "/studenttestmenu", method = RequestMethod.GET)
	public String testMenuGet() {
		return "studenttestmenu";
	}
	
	@RequestMapping(path = "/testexam", method = RequestMethod.GET)
	public String testexGet() {
		return "testexam";
	}
	
	@RequestMapping(path = "/studenteval", method = RequestMethod.GET)
	public String evalGet() {
		return "studenteval";
	}
	
	@RequestMapping(path = "/studenteval", method = RequestMethod.POST)
	public String evalPOST() {
		return "redirect:/studentmain";
	}
	
	@RequestMapping(path = "/studentmessage", method = RequestMethod.GET)
	public String mesGet() {
		return "studentmessage";
	}
	
}
