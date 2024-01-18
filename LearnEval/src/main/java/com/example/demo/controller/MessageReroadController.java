//package com.example.demo.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class MessageReroadController {
//
//	@Autowired
//    JdbcTemplate jdbcTemplate;
//	
//	@RequestMapping(path = "/your-server-endpoint/{roomID}", method = RequestMethod.GET)
//    public String getUpdatedMessages(Model model,@PathVariable String roomID) {
//		
//		List<Map<String, Object>> resultList;
//
//        resultList = jdbcTemplate.queryForList("select * from message where roomID = ?;", roomID);
//
//        model.addAttribute("resultList", resultList);
//        model.addAttribute("roomID", roomID);
//        
//		return "teachermessage";
//		
//		
//    }
//}
//
