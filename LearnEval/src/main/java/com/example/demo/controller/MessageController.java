package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.service.SessionCheckService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessageController {

	@Autowired
	SessionCheckService check;

	@Autowired
	JdbcTemplate jdbcTemplate;

	// 生徒側メッセージホーム画面表示
	@RequestMapping(path = "/studentmessagehome", method = RequestMethod.GET)
	public String studentmessagehomeGET(Model model, HttpSession session) {
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if (session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> userList = new ArrayList<>();
		int i = 0;

		resultList = jdbcTemplate.queryForList("select * from room where studentID = ?;", session.getAttribute("studentID"));

		for (Map<String, Object> result : resultList) {
			String roomID = result.get("roomID").toString();
			Object teacherIDObject = result.get("teacherID");

			if (teacherIDObject != null) {
				String teacherID = teacherIDObject.toString();
				nameList = jdbcTemplate.queryForList("select name from teachers where teacherID = ?;", teacherID);

				for (Map<String, Object> name : nameList) {
					name.put("roomID", roomID);
					userList.add(i++, name);
				}
			}
		}

		model.addAttribute("resultList", userList);
		return "studentmessagehome";
	}

	// 教師側メッセージホーム画面表示
	@RequestMapping(path = "/teachermessagehome", method = RequestMethod.GET)
	public String teachermessagehomeGET(HttpSession session, Model model) {
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> userList = new ArrayList<>();
		int i = 0;

		resultList = jdbcTemplate.queryForList("select * from room where teacherID = ?;", session.getAttribute("teacherID"));

		for (Map<String, Object> result : resultList) {
			String roomID = result.get("roomID").toString();
			Object studentIDObject = result.get("studentID");

			if (studentIDObject != null) {
				String studentID = studentIDObject.toString();
				nameList = jdbcTemplate.queryForList("select name from students where studentID = ?;", studentID);

				for (Map<String, Object> name : nameList) {
					name.put("roomID", roomID);
					userList.add(i++, name);
				}
			}
		}

		model.addAttribute("resultList", userList);
		return "teachermessagehome";
	}

	// 生徒側新規room作成画面表示
	@RequestMapping(path = "/studentmessagenew", method = RequestMethod.GET)
	public String studentmessagenew(Model model, HttpSession session) {
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if (session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;

		resultList = jdbcTemplate.queryForList("select * from teachers order by name asc;");

		String i = (String) session.getAttribute("alert");

		if ("1".equals(i)) {
			model.addAttribute("alert", "1");
			session.setAttribute("alert", "0");
		}

		model.addAttribute("resultList", resultList);
		return "studentmessagenew";
	}

	// 生徒側新規room作成
	@RequestMapping(path = "/studentmessagenew/{teacherID}", method = RequestMethod.GET)
	public String studentmessagenewpost(HttpSession session, @PathVariable String teacherID) {
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、studentmainへ移行
			if (session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;

		resultList = jdbcTemplate.queryForList("select * from room where teacherID = ? and studentID  = ?;",
				teacherID, session.getAttribute("studentID"));

		if (!resultList.isEmpty()) {
			String i = "1";
			session.setAttribute("alert", i);
			return "redirect:/studentmessagenew";
		} else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);", teacherID,
					session.getAttribute("studentID"));
		}
		//新規room作成後、生徒側メッセージホーム画面へ移行
		int roomID = jdbcTemplate.queryForObject("select roomID from room where teacherID = ? and studentID = ?;", Integer.class, teacherID, session.getAttribute("studentID"));

		return "redirect:/studentmessage/" + roomID;
	}

	// 教師側新規room作成画面表示
	@RequestMapping(path = "/teachermessagenew", method = RequestMethod.GET)
	public String teachermessagenew(Model model, HttpSession session, String selectclass) {
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;

		if (selectclass != null) {
			resultList = jdbcTemplate.queryForList("select * from students where class = ? order by number asc;",
					selectclass);
		} else {
			resultList = jdbcTemplate.queryForList("select * from students order by number asc;");
		}

		String i = (String) session.getAttribute("alert");

		if ("1".equals(i)) {
			model.addAttribute("alert", "1");
			session.setAttribute("alert", "0");
		}

		model.addAttribute("resultList", resultList);
		return "teachermessagenew";
	}

	// 教師側新規room作成
	@RequestMapping(path = "/teachermessagenew/{studentID}", method = RequestMethod.GET)
	public String teachermessagenewpost(Model model, HttpSession session, @PathVariable String studentID) {
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;

		resultList = jdbcTemplate.queryForList(
				"select * from room where teacherID = ? and studentID = ?;", session.getAttribute("teacherID"),
				studentID);

		if (!resultList.isEmpty()) {
			String i = "1";
			session.setAttribute("alert", i);
			return "redirect:/teachermessagenew";
		} else {
			jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",
					session.getAttribute("teacherID"), studentID);
		}
		
		//新規room作成後、教員側メッセージホーム画面へ移行
		int roomID = jdbcTemplate.queryForObject("select roomID from room where teacherID = ? and studentID = ?;", Integer.class, session.getAttribute("teacherID"), studentID);

		return "redirect:/teachermessage/"+roomID;
	}

	// 生徒側メッセージ画面表示
	@RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.GET)
	public String studentGet(Model model, @PathVariable String roomID, HttpSession session) {
		
		List<Map<String, Object>> studentID = jdbcTemplate.queryForList("select studentID from room where roomID = ?", roomID);
		
		
		//studentIDとsessionに入っているstudentIDが一致しない場合、sessionErrorへ移行
		if (!studentID.get(0).get("studentID").equals(session.getAttribute("studentID"))) {
			return "redirect:/sessionErrorS";
		}
		
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if (session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;

		nameList = jdbcTemplate.queryForList("select teacherID from room where roomID = ?", roomID);

		if (!nameList.isEmpty()) {
			Map<String, Object> teacherIDMap = nameList.get(0);
			Object teacherIDObject = teacherIDMap.get("teacherID");

			String teacherID = (teacherIDObject != null) ? teacherIDObject.toString() : null;

			nameList = jdbcTemplate.queryForList("select name from teachers where teacherID = ?", teacherID);

			if (!nameList.isEmpty()) {
				Map<String, Object> nameMap = nameList.get(0);
				Object nameObject = nameMap.get("name");

				String name = (nameObject != null) ? nameObject.toString() : null;

				model.addAttribute("name", name);
			}
		}

		resultList = jdbcTemplate.queryForList("select * from message where roomID = ? order by messageID asc;", roomID);

		for (Map<String, Object> result : resultList) {
			String daytime = result.get("daytime").toString();
			LocalDateTime dateTime = LocalDateTime.parse(daytime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
			result.put("daytime", formattedDateTime);
		}

		model.addAttribute("resultList", resultList);
		model.addAttribute("roomID", roomID);

		return "studentmessage";
	}

	// 生徒側メッセージ送信
	@RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.POST)
	public String studentPost(@PathVariable String roomID, String messageInput, HttpSession session) {
		// studentIDがない場合、sessionErrorへ移行
		if (check.studentSessionCheck(session)) {

			// teacherIDがある場合、teachermainへ移行
			if (session.getAttribute("teacherID") != null) {
				return "redirect:/sessionErrorT";
			}

			return "redirect:/sessionError";
		}

		if (messageInput.isEmpty()) {
			return "redirect:/studentmessage/" + roomID;
		}

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = now.format(formatter);

		jdbcTemplate.update("INSERT INTO message (daytime, senderID, roomID, text) values( ?, '1', ?, ?);",
				formattedDateTime, roomID, messageInput);

		return "redirect:/studentmessage/" + roomID;
	}

	// 教師側メッセージ画面表示
	@RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.GET)
	public String teacherGet(Model model, @PathVariable String roomID, HttpSession session) {
		
        List<Map<String, Object>> teacherID = jdbcTemplate.queryForList("select teacherID from room where roomID = ?", roomID);
		
		//teacherIDとsessionに入っているteacherIDが一致しない場合、sessionErrorへ移行
		if (!teacherID.get(0).get("teacherID").equals(session.getAttribute("teacherID"))) {
			return "redirect:/sessionError";
		}
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;

		nameList = jdbcTemplate.queryForList("select studentID from room where roomID = ?", roomID);

		if (!nameList.isEmpty()) {
			Map<String, Object> studentIDMap = nameList.get(0);
			Object studentIDObject = studentIDMap.get("studentID");

			String studentID = (studentIDObject != null) ? studentIDObject.toString() : null;

			nameList = jdbcTemplate.queryForList("select name from students where studentID = ?", studentID);

			if (!nameList.isEmpty()) {
				Map<String, Object> nameMap = nameList.get(0);
				Object nameObject = nameMap.get("name");

				String name = (nameObject != null) ? nameObject.toString() : null;

				model.addAttribute("name", name);
			}
		}

		resultList = jdbcTemplate.queryForList("select * from message where roomID = ? order by messageID asc;", roomID);

		for (Map<String, Object> result : resultList) {
			String daytime = result.get("daytime").toString();
			LocalDateTime dateTime = LocalDateTime.parse(daytime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
			result.put("daytime", formattedDateTime);
		}

		
		model.addAttribute("resultList", resultList);
		model.addAttribute("roomID", roomID);

		return "teachermessage";
	}

	// 教師側メッセージ送信
	@RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.POST)
	public String teacherPost(@PathVariable String roomID, String messageInput, HttpSession session) {
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		if (messageInput.isEmpty()) {
			return "redirect:/teachermessage/" + roomID;
		}

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = now.format(formatter);

		jdbcTemplate.update("INSERT INTO message (daytime, senderID, roomID, text) values( ?, '0', ?, ?);",
				formattedDateTime, roomID, messageInput);

		return "redirect:/teachermessage/" + roomID;
	}
}
