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

import jakarta.servlet.http.HttpSession;

@Controller
public class MessageController {
	
	
	String i ="0";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/studentmessagehome", method = RequestMethod.GET)
    public String studentmessagehome(Model model) {
        List<Map<String, Object>> resultList;
        List<Map<String, Object>> nameList;
        List<Map<String, Object>> userList = new ArrayList<>(); // Initialize userList
        int i = 0;

        resultList = jdbcTemplate.queryForList("select * from room where studentID = '2201009';"/*session.getAttribute("studentID")*/);

        // resultList をイテレートし、studentID の値を抽出
        for (Map<String, Object> result : resultList) {

            String roomID = result.get("roomID").toString();

            // マップ内での "studentID" のキーを仮定しています
            Object teacherIDObject = result.get("teacherID");

            // studentIDObject が null でないか確認してから変換を試みます
            if (teacherIDObject != null) {
                String teacherID = teacherIDObject.toString();
                nameList = jdbcTemplate.queryForList("select name from teachers where teacherID = ?;", teacherID);

                for (Map<String, Object> name : nameList) {

                    //roomIDをキーとして要素追加
                    name.put("roomID", roomID);
                    //userListにroomIDとname追加
                    userList.add(i++, name);
                }
            }
        }

        model.addAttribute("resultList", userList);

        return "studentmessagehome";
    }

    @RequestMapping(path = "/teachermessagehome", method = RequestMethod.GET)
    public String teachermessagehome(HttpSession session, Model model) {

        List<Map<String, Object>> resultList;
        List<Map<String, Object>> nameList;
        List<Map<String, Object>> userList = new ArrayList<>(); // Initialize userList
        int i = 0;

        resultList = jdbcTemplate.queryForList("select * from room where teacherID = '20350';"/*session.getAttribute("teacherID")*/);

        // resultList をイテレートし、studentID の値を抽出
        for (Map<String, Object> result : resultList) {

            String roomID = result.get("roomID").toString();

            // マップ内での "studentID" のキーを仮定しています
            Object studentIDObject = result.get("studentID");

            // studentIDObject が null でないか確認してから変換を試みます
            if (studentIDObject != null) {
                String studentID = studentIDObject.toString();
                nameList = jdbcTemplate.queryForList("select name from students where studentID = ?;", studentID);

                for (Map<String, Object> name : nameList) {

                    //roomIDをキーとして要素追加
                    name.put("roomID", roomID);
                    //userListにroomIDとname追加
                    userList.add(i++, name);
                }
            }
        }

        model.addAttribute("resultList", userList);

        return "teachermessagehome";
    }

    @RequestMapping(path = "/studentmessagenew", method = RequestMethod.GET)
    public String studentmessagenew(Model model,HttpSession session) {
    	
    	List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from teachers order by name asc;");
        
        
        i = (String) session.getAttribute("alert");
        
        if(i=="1") {
        	model.addAttribute("alert","1");
        	
        	session.setAttribute("alert", "0");
        }

        model.addAttribute("resultList", resultList);
    	
        return "studentmessagenew";
    }

    @RequestMapping(path = "/studentmessagenew/{teacherID}", method = RequestMethod.GET)
    public String studentmessagenewpost(HttpSession session,@PathVariable String teacherID) {

        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from room where teacherID = ? and studentID  = ?;",teacherID , 2201009 /*session.getAttribute("studentID")*/);

        if (!resultList.isEmpty()) {
        	i = "1";
            session.setAttribute("alert", i); //すでルームがあることを伝える
            return "redirect:/studentmessagenew";
        } else {
            jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);", teacherID, 2201009/*session.getAttribute("studentID")*/);
        }

        return "redirect:/studentmessagehome";
    }

    @RequestMapping(path = "/teachermessagenew", method = RequestMethod.GET)
    public String teachermessagenew(Model model,HttpSession session) {

        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from students order by number asc;");
        
        
        i = (String) session.getAttribute("alert");
        
        if(i=="1") {
        	model.addAttribute("alert","1");
        	
        	session.setAttribute("alert", "0");
        }

        model.addAttribute("resultList", resultList);

        return "teachermessagenew";
    }

    @RequestMapping(path = "/teachermessagenew/{studentID}", method = RequestMethod.GET)
    public String teachermessagenewpost(Model model, HttpSession session, @PathVariable String studentID) {

        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from room where teacherID = 20350 and studentID = ?;", studentID /*,session.getAttribute("teacherID")*/);

        if (!resultList.isEmpty()) {
            i = "1";
            session.setAttribute("alert", i); //すでルームがあることを伝える
            return "redirect:/teachermessagenew";
        } else {
            jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);", 20350 /*session.getAttribute("teacherID")*/, studentID);
        }

        return "redirect:/teachermessagehome";
    }

    //生徒側個人メッセージ画面
    @RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.GET)
    public String studentGet(Model model, @PathVariable String roomID) {

        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from message where roomID = ?;", roomID);

        model.addAttribute("resultList", resultList);

        return "studentmessage";
    }

    @RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.POST)
    public String studentPost(@PathVariable String roomID, String messageInput) {

        if (messageInput.isEmpty()) {
            return "redirect:/studentmessage/" + roomID;
        }

//      if (isAllHalfWidth(messageInput)) {
//          return "redirect:/studentmessage/"+roomID;
//        } 

        // 現在の日時を取得
        LocalDateTime now = LocalDateTime.now();

        // 日時のフォーマットを指定
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // フォーマットを適用して文字列として表示
        String formattedDateTime = now.format(formatter);

        jdbcTemplate.update("INSERT INTO message (daytime, senderID, roomID, text) values( ?, '1', ?, ?);", formattedDateTime, roomID, messageInput);

        return "redirect:/studentmessage/" + roomID;
    }

    //先生側個人メッセージ画面
    @RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.GET)
    public String teacherGet(Model model, @PathVariable String roomID) {

        //SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> resultList;

        //SELECT文の実行
        resultList = jdbcTemplate.queryForList("select * from message where roomID = ?;", roomID);

        model.addAttribute("resultList", resultList);

        model.addAttribute("roomID", roomID);

        return "teachermessage";
    }

    @RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.POST)
    public String teacherPost(@PathVariable String roomID, String messageInput) {

        if (messageInput.isEmpty()) {
            return "redirect:/teachermessage/" + roomID;
        }

//      if (isAllHalfWidth(messageInput)) {
//          return "redirect:/teachermessage/"+roomID;
//        } 

        // 現在の日時を取得
        LocalDateTime now = LocalDateTime.now();

        // 日時のフォーマットを指定
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // フォーマットを適用して文字列として表示
        String formattedDateTime = now.format(formatter);

        jdbcTemplate.update("INSERT INTO message (daytime, senderID, roomID, text) values( ?, '0', ?, ?);", formattedDateTime, roomID, messageInput);

        return "redirect:/teachermessage/" + roomID;
    }

    // 文字列がすべて半角かどうかを判定
    private static boolean isAllHalfWidth(String input) {
        String regex = "[\\x00-\\x7F]*";
        return input.matches(regex);
    }
}
