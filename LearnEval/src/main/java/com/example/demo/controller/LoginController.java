package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login() {
		
		return "login";
	}
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String checkUser(String type,String id,String pass,Model model,HttpSession session) {
		
		if("student".equals(type)) {
//			
//			//SELECT文の結果をしまうためのリスト
//			List<Map<String, Object>> result;
//
//			//SELECT文の実行
//			result = jdbcTemplate.queryForList("select * from students where studentID = ? and pass = ?",id,pass);
//			
//			if (!result.isEmpty()) {
//			
//			Map<String, Object> user = result.get(0);
//			
//			String name = (String)user.get("name");
//			int studentID = ((Number) user.get("studentID")).intValue();
//			
//			System.out.println(name);
//			
//			session.setAttribute("name",name);
//			session.setAttribute("studentID",studentID);
//			
			return "redirect:/studentmain";
			}
	/*}*/
		else if(type.equals("teacher")) {
			
//			
//			//SELECT文の結果をしまうためのリスト
//			List<Map<String, Object>> result;
//			
//			//SELECT文の実行
//			result = jdbcTemplate.queryForList("select * from teachers where teacherID = ? and pass = ?",id,pass);
//			
//			if (!result.isEmpty()) {
//			
//			Map<String, Object> user = result.get(0);
//			
//			String name = (String)user.get("name");
//			int teacherID = ((Number) user.get("teacherID")).intValue();
//
//			System.out.println(name);
//			
//			session.setAttribute("name",name);
//			session.setAttribute("teacherID",teacherID);
			
			return "redirect:/teachermain";
			}
		/*}*/
					else{
			model.addAttribute("login","<script>alert('一致するIDがありません');</script>");
			}
			return "login";
	}
	
	@RequestMapping(path = "/forget", method = RequestMethod.GET)
	public String forget() {

		return "forget";
	}
	@RequestMapping(path = "/forget", method = RequestMethod.POST)
	public String editPass(String id,String pass,Model model) {
	
		model.addAttribute("login","<script>alert('パスワードが変更されました');</script>");
		return "redirect:/login";
	}
}
