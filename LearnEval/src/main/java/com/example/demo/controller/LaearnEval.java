package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LaearnEval {

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login() {

		return "login";
	}
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String checkUser(String id,String pass) {
		if(id == "seito") {
			return "studentlogin";
		}else if(id == "sense") {
			return "teacherlogin";
		}
		return "login";
	}
}
