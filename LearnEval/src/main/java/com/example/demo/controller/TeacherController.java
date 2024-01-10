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

@Controller
public class TeacherController {

	@Autowired
	JdbcTemplate jdbcTemplate;

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
			return "redirect:/teachermessagehome";

		}
		return "/studentmain";
	}

	@RequestMapping(path = "/teachertestmenu", method = RequestMethod.GET)
	public String testMenuGet(Model model) {

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from tests");
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);


		return "teachertestmenu";
	}


	@RequestMapping(path = "/teacherstumenu", method = RequestMethod.GET)
	public String stuMenuGet() {
		return "teacherstumenu";
	}

	@RequestMapping(path = "/testedit/{num}", method = RequestMethod.GET)
	public String testexGet(Model model,@PathVariable String num) {

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> c_result;

		//SELECT文の実行
		q_result = jdbcTemplate.queryForList("select * from tests where questionID = ?",num);
		//SELECT文の実行
		c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionID = ? ORDER BY selectnumber asc",num);

		Map<String, Object> question = q_result.get(0);

		String image = (String)question.get("image");
		int number = ((Number) question.get("questionID")).intValue();

		model.addAttribute("image", image);
		model.addAttribute("number", number);
		model.addAttribute("question",c_result);

		return "testedit";
	}


}
