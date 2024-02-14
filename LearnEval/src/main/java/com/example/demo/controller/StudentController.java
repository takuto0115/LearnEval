package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.SessionCheckService;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

	@Autowired
	SessionCheckService check;


	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentmain", method = RequestMethod.GET)
	public String mainGet(HttpSession session,Model model) {

		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、アラートを表示してからteachermainへ移行
			if(session.getAttribute("teacherID") != null) {

				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		return "studentmain";
	}

	@RequestMapping(path = "/studentmain", method = RequestMethod.POST)
	public String mainPost(String page) {
		switch (page) {
		case "テスト一覧":
			return "redirect:/studenttestmenu";
		case "成績一覧":
			return "redirect:/studenteval";
		case "メッセージ一覧":
			return "redirect:/studentmessagehome";
		}
		return "/studentmain";
	}

	@RequestMapping(path = "/studenttestmenu", method = RequestMethod.GET)
	public String testMenuGet(Model model, HttpSession session) {

		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if(session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from testtitle");

		String newNum = Integer.toString(tests.size() + 1);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);
		model.addAttribute("newNum", newNum);

		return "studenttestmenu";
	}

	@RequestMapping(path = "/testpage/{num}", method = RequestMethod.GET)
	public String testexGet(Model model, @PathVariable String num, HttpSession session) {

		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if(session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;

		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> c_result;

		// SELECT文の実行
		String title= jdbcTemplate.queryForObject("select title from testtitle where questionNumber = ?",String.class, num);

		// SELECT文の実行
		q_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?", num);

		// SELECT文の実行
		c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",
				num);

		Map<String, Object> question = q_result.get(0);

		int number = ((Number) question.get("questionNumber")).intValue();

		model.addAttribute("title", title);
		model.addAttribute("image", q_result);
		model.addAttribute("number", number);
		model.addAttribute("question", c_result);

		return "testpage";
	}

	@RequestMapping(path = "/test", method = RequestMethod.POST)
	public String test(HttpSession session, String select1, String select2, String select3, String select4, String select5,
			String select6, String num, String selectNumber, Model model) {

		String[] select = { select1, select2, select3, select4, select5, select6 };

		// selectの中身がNullの場合、""に変換する
		for (int i = 0; i < select.length; i++) {
			if (select[i] == null) {
				select[i] = "";
			}
		}

		int i = 0;
		double count = 0;
		String studentID = (String) session.getAttribute("studentID");
		String[] ratio = { "×", "×", "×", "×", "×", "×" };

		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;

		q_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",
				num);

		for (Map<String, Object> Checking : q_result) {
			String answer = Checking.get("answer").toString();
			if (select[i].equals(answer)) {
				ratio[i] = "〇";
			}
			i++;
		}

		for (int j = 0; j < i; j++) {
			if (ratio[j].equals("〇")) {
				count++;
			}
		}

		double answer_rate = count / i;

		int answer_rate_int = (int) (answer_rate * 100);

		// 現在時刻をnowに保存する
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String endTime = now.format(formatter);

		// 学生ID、問題ID、選んだ六つの選択肢(select_first-sixth)、正答率、終了時間をevalテーブルに保存する
		jdbcTemplate.update(
				"insert into eval (studentID,questionNumber,select_first,select_second,select_third,select_fourth,select_fifth,select_sixth,answer_rate,end_time) value (?,?,?,?,?,?,?,?,?,?);",
				studentID, num, select1, select2, select3, select4, select5, select6, answer_rate_int, endTime);

		// select1から6をlistに保存する
		List<String> selectList = List.of(select);

		// selectListから""を削除する
		selectList = selectList.stream().filter(s -> !s.equals("")).collect(Collectors.toList());

		List<String> ratioList = List.of(ratio);

		// ratioListをi番目までだけにする
		ratioList = ratioList.subList(0, i);
		
		//問に対応したEndtimeの新しい順にデータを取得
		//resultの三番目のendotimeを取得
		//endtime3より古いデータを削除
		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM eval WHERE studentID = ? AND questionNumber = ? ORDER BY end_time desc",studentID,num);
		String endtime3 = (String) result.get(2).get("end_time").toString();
		jdbcTemplate.update("DELETE FROM eval WHERE studentID = ? AND questionNumber = ? AND end_time < ?",studentID,num,endtime3);

		// ratioListをmodelに保存する
		model.addAttribute("ratioList", ratioList);

		// selectListをmodelに保存する
		model.addAttribute("selectList", selectList);

		// answer_rateをmodelに保存する
		model.addAttribute("answer_rate", answer_rate_int);

		// 終了時刻をmodelに保存する
		model.addAttribute("endTime", endTime);

		return "testeval";
	}

	@RequestMapping(path = "/studenteval", method = RequestMethod.GET)
	public String evalGet(HttpSession session,Model model,
			@RequestParam(name = "type", required = false)String type) {
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {
			// teacherIDがある場合、teachermainへ移行
			if(session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}
			return "redirect:/sessionError";
		}
		//typeがnullの場合,""に変換する
		if (type == null) {
			type = "";
		}
		System.out.println(type);
		String studentID =  (String) session.getAttribute("studentID");
		List<Map<String, Object>> result = new ArrayList<>();
		if (type.equals("questionNumber")) {
			result = jdbcTemplate.queryForList("SELECT questionNumber,answer_rate,end_time FROM eval WHERE studentID = ? order by questionNumber asc", studentID);
		}else {
			result = jdbcTemplate.queryForList("SELECT questionNumber,answer_rate,end_time FROM eval WHERE studentID = ? order by end_time asc", studentID);
		}
		model.addAttribute("test_list", result);

		return "studenteval";
	}
}
