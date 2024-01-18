package com.example.demo.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class DBController {
	
	//DBへつなぐために必要
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/db", method = RequestMethod.GET)
	public String login() {

		return "db";
	}
	@RequestMapping(path = "/db", method = RequestMethod.POST)
	public String fourthp( MultipartFile upimage)
			throws IOException {

		//アップロードされたファイルをバイトデータに変換する。
		byte[] byteData = upimage.getBytes();

		//Base64に変換する。
		//（変数「encodedImage」に画像が格納されてる）
		String encodedImage = Base64.getEncoder().encodeToString(byteData);

		//DBに画面から入力されたデータを登録する。
		jdbcTemplate.update("insert into tests (questionNumber,image,language) value ('2',?,'Java');",encodedImage);

		return "db";
	}
}