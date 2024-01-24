package com.example.demo.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class TeacherController {

	SessionCheck check = new SessionCheck();
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/teachermain", method = RequestMethod.GET)
	public String mainGet(HttpSession session) {
		System.out.println("teachermain");
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (check.sessionCheck(session)) {
			return "redirect:/sessionError";
		}
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
		return "/teachermain";
	}

	@RequestMapping(path = "/teachertestmenu", method = RequestMethod.GET)
	public String testMenuGet(Model model,HttpSession session) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (check.sessionCheck(session)) {
			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from tests");
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);


		return "teachertestmenu";
	}


	@RequestMapping(path = "/teacherstumenu", method = RequestMethod.GET)
	public String stuMenuGet(HttpSession session) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (check.sessionCheck(session)) {
			return "redirect:/sessionError";
		}
		return "teacherstumenu";
	}

	@RequestMapping(path = "/testedit/{num}", method = RequestMethod.GET)
	public String testexGet(Model model,@PathVariable String num,HttpSession session) {
		
		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (check.sessionCheck(session)) {
			return "redirect:/sessionError";
		}

		session.removeAttribute("num");
		session.setAttribute("num",num);
		
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

		return "testedit";
	}

	@RequestMapping(path = "/testeditimg/{num}", method = RequestMethod.POST)
	public String edit_image(MultipartFile upimage,@PathVariable String num)
			throws IOException {

		//アップロードされたファイルをバイトデータに変換する。
		byte[] byteData = upimage.getBytes();

		//Base64に変換する。
		//（変数「encodedImage」に画像が格納されてる）
		String encodedImage = Base64.getEncoder().encodeToString(byteData);

		//DBに画面から入力されたデータを登録する。
		jdbcTemplate.update("insert into tests (questionNumber,image,language) value ('2',?,'Java');",encodedImage);

		return "/testedit";
	}
	
	@RequestMapping(path = "/testedit_q", method = RequestMethod.POST)
	public String edit_q(String first,String second,String third,String forth,String answer_num,HttpSession session)
			throws IOException {
		
		String num = session.getAttribute("num").toString();
		
		String[] question = {first,second,third,forth};
		
		int ans = Integer.parseInt(answer_num);
		
		String answer = question[ans - 1];

		//DBに画面から入力されたデータを登録する。
		jdbcTemplate.update("update choices set select_first = ?,select_sec = ?,select_third = ?,select_forth = ?,"
				+ "answer = ? where questionNumber = ? and selectNumber"
				,question[0],question[1],question[2],question[3],answer,num);

		return "/testedit/" + num;
	}
	
}
