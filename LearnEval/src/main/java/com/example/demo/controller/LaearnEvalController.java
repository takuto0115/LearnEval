package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class LaearnEvalController {

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login() {

		return "login";
	}
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String checkUser(String type,String id,String pass,Model model) {
		if(type.equals("student")/*&&id.equals("id")*/) {
			return "redirect:/studentmain";
		}else if(type.equals("teacher")) {
			return "redirect:/teachermain";
		}else{
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
