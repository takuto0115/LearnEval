package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MessageController {
	
	@RequestMapping(path = "/studentmessage", method = RequestMethod.GET)
	public String studentGet() {
		return "studentmessage";
	}
	
	@RequestMapping(path = "/teachermessage", method = RequestMethod.GET)
	public String teacherGet() {
		return "teachermessage";
	}
	
}
