package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StudentController {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
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
	
	@RequestMapping(path = "/testpage", method = RequestMethod.GET)
	public String testexGet(Model model) {


		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from tests");

		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);
		
		return "testpage";
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
