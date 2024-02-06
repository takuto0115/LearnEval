package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	//evalテーブルはからstudentIDで検索するメソッドを作成する。
	public List<Map<String, Object>> findByStudentID(String studentID) {
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT questionNumber,answer_rate,end_time FROM eval WHERE studentID = ? order by end_time asc", studentID);
		
		return result;
	}
	

}
