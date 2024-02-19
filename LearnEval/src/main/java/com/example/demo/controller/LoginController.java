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
public class LoginController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(HttpSession session) {

			session.removeAttribute("name");
			session.removeAttribute("studentID");
			session.removeAttribute("teacherID");

		return "login";
	}
	
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String checkUser(String name,String id,Model model,HttpSession session) {

		//入力されたIDとパスワードをもとにDBを検索
		List<Map<String, Object>> s_result = jdbcTemplate.queryForList("select * from students where name = ? and studentID = ?",name,id);
		List<Map<String, Object>> t_result = jdbcTemplate.queryForList("select * from teachers where teacherID = ? and name = ?",id,name);

			if (!s_result.isEmpty()) {

				Map<String, Object> user = s_result.get(0);

				String studentID = (String)user.get("studentID").toString();

				session.setAttribute("name",name);
				session.setAttribute("studentID",studentID);

				return "redirect:/studentmain";
			}
			if (!t_result.isEmpty()) {

				Map<String, Object> user = t_result.get(0);

				String teacherID = (String)user.get("teacherID").toString();

				session.setAttribute("name",name);
				session.setAttribute("teacherID",teacherID);

				return "redirect:/teachermain";
			}
		
		model.addAttribute("name", name);
		model.addAttribute("id", id);
		return "login";
	}
	
	@RequestMapping(path = "/sessionError", method = RequestMethod.GET)
	public String sessionError() {

		return "sessionError";
	}
	
	@RequestMapping(path = "/sessionErrorT", method = RequestMethod.GET)
	public String sessionErrorT() {

		return "sessionerrorT";
	}
	
	@RequestMapping(path = "/sessionErrorS", method = RequestMethod.GET)
	public String sessionErrorS() {

		return "sessionerrorS";
	}
}
