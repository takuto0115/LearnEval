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
	
	
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/studentmessagehome", method = RequestMethod.GET)
    public String studentmessagehomeGET(Model model, HttpSession session) {
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

    @RequestMapping(path = "/teachermessagehome", method = RequestMethod.GET)
    public String teachermessagehomeGET(HttpSession session, Model model) {
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

    @RequestMapping(path = "/studentmessagenew", method = RequestMethod.GET)
    public String studentmessagenew(Model model, HttpSession session) {
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

    @RequestMapping(path = "/studentmessagenew/{teacherID}", method = RequestMethod.GET)
    public String studentmessagenewpost(HttpSession session, @PathVariable String teacherID) {
        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList("select * from room where teacherID = ? and studentID  = ?;", teacherID,session.getAttribute("studentID"));

        if (!resultList.isEmpty()) {
            String i = "1";
            session.setAttribute("alert", i);
            return "redirect:/studentmessagenew";
        } else {
            jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);", teacherID,
                    session.getAttribute("studentID"));
        }

        return "redirect:/studentmessagehome";
    }

    @RequestMapping(path = "/teachermessagenew", method = RequestMethod.GET)
    public String teachermessagenew(Model model, HttpSession session,String selectclass) {
        List<Map<String, Object>> resultList;

        //プルダウンで選択されたselectclassをもとにstudentsテーブルから生徒を検索
        if(selectclass != null) {
        	resultList = jdbcTemplate.queryForList("select * from students where class = ? order by number asc;",selectclass);
        	}else {
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

    @RequestMapping(path = "/teachermessagenew/{studentID}", method = RequestMethod.GET)
    public String teachermessagenewpost(Model model, HttpSession session, @PathVariable String studentID) {
        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList(
                "select * from room where teacherID = ? and studentID = ?;", session.getAttribute("teacherID"),studentID);

        if (!resultList.isEmpty()) {
            String i = "1";
            session.setAttribute("alert", i);
            return "redirect:/teachermessagenew";
        } else {
            jdbcTemplate.update("INSERT INTO room (teacherID, studentID) VALUES(?, ?);",session.getAttribute("teacherID"), studentID);
        }

        return "redirect:/teachermessagehome";
    }

    @RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.GET)
    public String studentGet(Model model, @PathVariable String roomID) {
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

                System.out.println(name);
                
                model.addAttribute("name", name);

            }
        }

        resultList = jdbcTemplate.queryForList("select * from message where roomID = ?;", roomID);

        model.addAttribute("resultList", resultList);
        
        return "studentmessage";
    }

    @RequestMapping(path = "/studentmessage/{roomID}", method = RequestMethod.POST)
    public String studentPost(@PathVariable String roomID, String messageInput) {
        if (messageInput.isEmpty()) {
            return "redirect:/studentmessage/" + roomID;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        jdbcTemplate.update(
                "INSERT INTO message (daytime, senderID, roomID, text) values( ?, '1', ?, ?);", formattedDateTime,roomID, messageInput);

        return "redirect:/studentmessage/" + roomID;
    }

    @RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.GET)
    public String teacherGet(Model model, @PathVariable String roomID) {
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

        //resultListに入っているdaytime(2024-01-26T11:10:21)を01/26 11:10に変換
        for (Map<String, Object> result : resultList) {
            String daytime = result.get("daytime").toString(); // 2024-01-26T11:10:21
            LocalDateTime dateTime = LocalDateTime.parse(daytime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")); // 01/26 11:10
            result.put("daytime", formattedDateTime);
        }

        model.addAttribute("resultList", resultList);
        model.addAttribute("roomID", roomID);

        return "teachermessage";

    }

    @RequestMapping(path = "/teachermessage/{roomID}", method = RequestMethod.POST)
    public String teacherPost(@PathVariable String roomID, String messageInput) {
        if (messageInput.isEmpty()) {
            return "redirect:/teachermessage/" + roomID;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        jdbcTemplate.update("INSERT INTO message (daytime, senderID, roomID, text) values( ?, '0', ?, ?);",formattedDateTime, roomID, messageInput);

        return "redirect:/teachermessage/" + roomID;
    }

}
