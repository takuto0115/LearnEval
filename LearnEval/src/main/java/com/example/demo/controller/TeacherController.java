package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
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
		//questionNumberが被っているものを削除する
		for (int i = 0; i < tests.size(); i++) {
			Map<String, Object> map = tests.get(i);
			int questionNumber = (int) map.get("questionNumber");
			for (int j = i + 1; j < tests.size(); j++) {
				Map<String, Object> map2 = tests.get(j);
				int questionNumber2 = (int) map2.get("questionNumber");
				if (questionNumber == questionNumber2) {
					tests.remove(j);
					j--;
				}
			}
		}
		String newNum = Integer.toString(tests.size() + 1);
		System.out.println(newNum);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);
		model.addAttribute("new", newNum);


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

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> c_result;


		//SELECT文の実行
		q_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?",num);
		//SELECT文の実行
		c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",num);
		List<Integer> delNum  = new ArrayList<Integer>(); 
		//choiceの数分のナンバリング
		for (int i = 0; i < c_result.size(); i++) {
			System.out.println(i);
			delNum.add(i, i+1);
		}

		model.addAttribute("delNum", delNum);
		model.addAttribute("image", q_result);
		model.addAttribute("number", num);
		model.addAttribute("question",c_result);

		return "testedit";
	}

	@RequestMapping(path = "/testeditimg", method = RequestMethod.POST)
	public String edit_image(MultipartFile upimage,String num,String imagenum)
			throws IOException {

		if (upimage==null || upimage.isEmpty()) {
			return "redirect:/testedit/" + num;
		} else if (imagenum.equals("new")) {
			//アップロードされたファイルをバイトデータに変換する。
			byte[] byteData = upimage.getBytes();

			//Base64に変換する。
			//（変数「encodedImage」に画像が格納されてる）
			String encodedImage = Base64.getEncoder().encodeToString(byteData);

			List<Map<String, Object>> i_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?",num);

			int imageNumber = i_result.size() + 1;

			Map<String, Object> map = i_result.get(0);

			String language = (String) map.get("language");

			int questionNumber = (int)map.get("questionNumber");

			//DBに画面から入力されたデータを登録する。
			jdbcTemplate.update("insert into tests (questionNumber,image,language,imageNumber) value (?,?,?,?);"
					,questionNumber ,encodedImage,language,imageNumber);

		}else {
			//アップロードされたファイルをバイトデータに変換する。
			byte[] byteData = upimage.getBytes();

			//Base64に変換する。
			//（変数「encodedImage」に画像が格納されてる）
			String encodedImage = Base64.getEncoder().encodeToString(byteData);

			//DBに画面から入力されたデータを更新する。
			jdbcTemplate.update("update tests set image = ? where questionNumber = ?",encodedImage,num);
		}

		return "redirect:/testedit/" + num;
	}

	@RequestMapping(path = "/testedit_q", method = RequestMethod.POST)
	public String edit_q(String first,String second,String third,String forth,String answer_num,String quenum,String num,HttpSession session)
			throws IOException {

		String[] question = {first,second,third,forth};

		int ans = Integer.parseInt(answer_num);

		String answer = question[ans - 1];

		if(num.equals("new")) {

			List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select * from choices where questionNumber = ?",quenum);

			num = Integer.toString(n_result.size() + 1);

			jdbcTemplate.update(
					"insert into choices (questionNumber,selectNumber,select_first,select_sec,select_third,select_forth,answer) value (?,?,?,?,?,?,?)",
					quenum, num, question[0], question[1], question[2], question[3], answer);
		}else {

			//DBに画面から入力されたデータを登録する。
			jdbcTemplate.update("update choices set select_first = ?,select_sec = ?,select_third = ?,select_forth = ?,"
					+ "answer = ? where questionNumber = ? and selectNumber = ?"
					,question[0],question[1],question[2],question[3],answer,quenum,num);
		}

		return "redirect:/testedit/" + quenum;
	}

	@RequestMapping(path = "/delete", method = RequestMethod.POST)
	public String delete(String num, String quenum, HttpSession session)
			throws IOException {

		jdbcTemplate.update("delete from choices where questionNumber = ? and selectNumber = ?", quenum, num);

		//今まであった設問の数をselectNumber昇順で取得
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select * from choices where questionNumber = ? ORDER BY selectNumber asc",quenum);
		//サイズを取り出してナンバリングをする
		int size = n_result.size();
		//ナンバリング
		for (int i = 0; i < size; i++) {
			Map<String, Object> map =n_result.get(i);
			int selectNumber = (int) map.get("selectNumber");
			jdbcTemplate.update("update choices set selectNumber = ? where questionNumber = ? and selectNumber = ?",
					i + 1, quenum, selectNumber);
		}
		return "redirect:/testedit/" + quenum;

	}

}
