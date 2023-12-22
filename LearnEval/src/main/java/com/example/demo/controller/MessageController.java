package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
public class MessageController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentmessagehome", method = RequestMethod.GET)
	public String studentmessagehome() {


		return "studentmessagehome";
	}

	@RequestMapping(path = "/teachermessagehome", method = RequestMethod.GET)
	public String teachermessagehome(HttpSession session,Model model) {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> userList = new ArrayList<>(); // Initialize userList

		resultList = jdbcTemplate.queryForList("select * from room where teacherID = '20350';"/*session.getAttribute("teacherID")*/);

		// resultList をイテレートし、studentID の値を抽出
		for (Map<String, Object> result : resultList) {

		    // マップ内での "studentID" のキーを仮定しています
		    Object studentIDObject = result.get("studentID");

		    // studentIDObject が null でないか確認してから変換を試みます
		    if (studentIDObject != null) {
		        String studentID = studentIDObject.toString();
		        nameList = jdbcTemplate.queryForList("select name from students where studentID = ?;", studentID);

		        for (Map<String, Object> name : nameList) {

		            // マップ内での "name" のキーを仮定しています (修正済み)
		            Object studentNameObject = name.get("name");

		            if (studentNameObject != null) {
		                String studentName = studentNameObject.toString();
		                userList.add(Collections.singletonMap("name", studentName));
		            }
		        }
		    }

		}
		
		
		
		model.addAttribute("resultList",resultList);
		
		model.addAttribute("userList",userList);



		return "teachermessagehome";
	}


	@RequestMapping(path = "/studentmessagenew", method = RequestMethod.GET)
	public String studentmessagenew() {


		return "studentmessagenew";
	}

	@RequestMapping(path = "/studentmessagenew", method = RequestMethod.POST)
	public String studentmessagenewpost(HttpSession session,String teacherID) {


		List<Map<String, Object>> resultList;


		resultList = jdbcTemplate.queryForList("select * from room where studentID = ? and teacherID = ?;",session.getAttribute("studentID"),teacherID);

		if(!resultList.isEmpty()) {
			//model.addAttribute("no",); すでルームがあることを伝える
			return "studentmessagehome";
		}else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",session.getAttribute("studentID"),teacherID);
		}

		return "redirect:/studentmessagehome";
	}

	@RequestMapping(path = "/teachermessagenew", method = RequestMethod.GET)
	public String teachermessagenew() {


		return "teachermessagenew";
	}

	@RequestMapping(path = "/teachermessagenew", method = RequestMethod.POST)
	public String teachermessagenewpost(Model model,HttpSession session,String studentID) {


		List<Map<String, Object>> resultList;


		resultList = jdbcTemplate.queryForList("select * from room where studentID = ? and teacherID = ?;",studentID,session.getAttribute("teacherID"));

		if(!resultList.isEmpty()) {
			//model.addAttribute("no",); すでルームがあることを伝える
			return "teachermessagehome";
		}else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",session.getAttribute("teacherID"),studentID);
		}



		return "redirect:/teachermessagehome";
	}


	//生徒側個人メッセージ画面
	@RequestMapping(path = "/studentmessage", method = RequestMethod.GET)
	public String studentGet(Model model) {


		List<Map<String, Object>> resultList;


		resultList = jdbcTemplate.queryForList("select * from message where roomID = 01;");

		model.addAttribute("resultList",resultList);

		return "studentmessage";
	}

	@RequestMapping(path = "/studentmessage", method = RequestMethod.POST)
	public String studentPost() {


		return "studentmessage";
	}



	//先生側個人メッセージ画面
	@RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.GET)
	public String teacherGet(Model model,@PathVariable String roomID) {

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from message where roomID = ?;",roomID);

		model.addAttribute("resultList",resultList);

		return "teachermessage";
	}

	@RequestMapping(path = "/teachermessage", method = RequestMethod.POST)
	public String teacherPost(HttpSession session,String messageInput,String studentID,String roomID) {

		// 現在の日時を取得
		LocalDateTime now = LocalDateTime.now();

		// 日時のフォーマットを指定
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// フォーマットを適用して文字列として表示
		String formattedDateTime = now.format(formatter);

		//jdbcTemplate.update("INSERT INTO message (teacherID, studentID, daytime, senderID, roomID, text) values(?, ?, ?, '0', ?, ?);",session.getAttribute("teacherID"),studentID,formattedDateTime,roomID,messageInput);

		jdbcTemplate.update("INSERT INTO message (teacherID, studentID, daytime, senderID, roomID, text) values('01', '2201009',?, '0', '01', ?);",formattedDateTime,messageInput);

		return "redirect:/teachermessage";
	}

}
