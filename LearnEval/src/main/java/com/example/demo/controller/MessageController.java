package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessageController {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@RequestMapping(path = "/studentmessagehome", method = RequestMethod.GET)
	public String studentmessagehome() {
		
		
		return "studentmessagehome";
	}
	
	@RequestMapping(path = "/teachermessagehome", method = RequestMethod.GET)
	public String teachermessagehome() {
		
		return "teachermessagehome";
	}
	
	
	@RequestMapping(path = "/studentmessagenew", method = RequestMethod.GET)
	public String studentmessagenew() {
		
		
		return "studentmessagenew";
	}
	
	@RequestMapping(path = "/studentmessagenew", method = RequestMethod.POST)
	public String studentmessagenewpost(HttpSession session) {
		
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from room where studentID = ? and teacherID = ?;",session.getAttribute("studentID"),teacherID);
				
		if(!resultList.isEmpty()) {
			//model.addAttribute("no",); すでルームがあることを伝える
			return "studentmessagehome";
		}else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",session.getAttribute("studentID"),teacherID);
		}
		
		return "redirect:/studentmessagehome";
	}
	
	@RequestMapping(path = "/teachermessagenew", method = RequestMethod.GET)
	public String teachermessagenew() {
		
		
		return "teachermessagenew";
	}
	
	@RequestMapping(path = "/teachermessagenew", method = RequestMethod.POST)
	public String teachermessagenewpost(Model model,HttpSession session) {
		
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from room where studentID = ? and teacherID = ?;",studentID,session.getAttribute("teacherID"));
						
		if(!resultList.isEmpty()) {
			//model.addAttribute("no",); すでルームがあることを伝える
			return "teachermessagehome";
		}else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",session.getAttribute("teacherID"),studentID);
		}
		
		
		
		return "redirect:/teachermessagehome";
	}
	
	
	
	@RequestMapping(path = "/studentmessage", method = RequestMethod.GET)
	public String studentGet(Model model) {
		
		//SELECT文の結果をしまうためのリスト
				List<Map<String, Object>> resultList;

				//SELECT文の実行
				resultList = jdbcTemplate.queryForList("select * from message where roomID = 01;");
				
				model.addAttribute("resultList",resultList);
		
		return "studentmessage";
	}
	
	@RequestMapping(path = "/studentmessage", method = RequestMethod.POST)
	public String studentPost() {
		
		
		return "studentmessage";
	}
	
	@RequestMapping(path = "/teachermessage", method = RequestMethod.GET)
	public String teacherGet() {
		return "teachermessage";
	}
	
	@RequestMapping(path = "/teachermessage", method = RequestMethod.POST)
	public String teacherPost() {
		return "teachermessage";
	}
	
}
