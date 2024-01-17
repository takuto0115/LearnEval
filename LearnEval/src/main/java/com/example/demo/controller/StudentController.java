package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

	boolean move;
	SessionCheck check = new SessionCheck();
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentmain", method = RequestMethod.GET)
	public String mainGet(HttpSession session) {
		
		move = check.sessionCheck(session);
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (move) {
			return "redirect:/sessionError";
		}
		
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
			return "redirect:/studentmessagehome";

		}
		return "/studentmain";
	}

	@RequestMapping(path = "/studenttestmenu", method = RequestMethod.GET)
	public String testMenuGet(Model model,HttpSession session) {
		
		move = check.sessionCheck(session);
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (move) {
			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from tests");
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);

		return "studenttestmenu";
	}

	@RequestMapping(path = "/testpage/{num}", method = RequestMethod.GET)
	public String testexGet(Model model,@PathVariable String num,HttpSession session) {
		
		move = check.sessionCheck(session);
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (move) {
			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> c_result;

		//SELECT文の実行
		q_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?",num);
		//SELECT文の実行
		c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",num);

		Map<String, Object> question = q_result.get(0);

		String image = (String)question.get("image");
		int number = ((Number) question.get("questionNumber")).intValue();

		model.addAttribute("image", image);
		model.addAttribute("number", number);
		model.addAttribute("question",c_result);

		return "testpage";
	}

	@RequestMapping(path = "/studenteval", method = RequestMethod.GET)
	public String evalGet(HttpSession session) {
		move = check.sessionCheck(session);
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (move) {
			return "redirect:/sessionError";
		}
		return "studenteval";
	}

	@RequestMapping(path = "/studenteval", method = RequestMethod.POST)
	public String evalPOST() {
		return "redirect:/studentmain";
	}

}
