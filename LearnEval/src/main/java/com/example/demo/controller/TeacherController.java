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

import com.example.demo.service.SessionCheckService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TeacherController {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	SessionCheckService check;

	@RequestMapping(path = "/teachermain", method = RequestMethod.GET)
	public String mainGet(HttpSession session) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}
		return "teachermain";
	}

	@RequestMapping(path = "/teachertestmenu", method = RequestMethod.GET)
	public String testMenuGet(Model model,HttpSession session) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;
		List<Map<String, Object>> genre_List;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from testtitle");
		genre_List = jdbcTemplate.queryForList("select genre from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < genre_List.size(); i++) {
			Map<String, Object> map = genre_List.get(i);
			String gen1 = (String) map.get("genre");
			for (int j = i + 1; j < genre_List.size(); j++) {
				Map<String, Object> map2 = genre_List.get(j);
				String gen2 = (String) map2.get("genre");
				if (gen1.equals(gen2)) {
					genre_List.remove(j);
					j--;
				}
			}
		}

		//結果を3等分する
		int size = tests.size();
		int size2 = size / 3;
		int size3 = size - size2;
		List<Map<String, Object>> test1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> test2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> test3 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < size; i++) {
			if (i < size2) {
				test1.add(tests.get(i));
			} else if (i < size3) {
				test2.add(tests.get(i));
			} else {
				test3.add(tests.get(i));
			}
		}
		System.out.println(size + " " + size2 + " " + size3);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("size", size);
		model.addAttribute("tests", tests);
		model.addAttribute("test1", test1);
		model.addAttribute("test2", test2);
		model.addAttribute("test3", test3);
		model.addAttribute("genre", genre_List);

		return "teachertestmenu";
	}

	@RequestMapping(path = "/testsearch", method = RequestMethod.GET)
	public String testSearchGet(Model model, HttpSession session,String genre) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> tests;
		List<Map<String, Object>> genre_List;

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from testtitle where genre = ?",genre);
		genre_List = jdbcTemplate.queryForList("select genre from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < genre_List.size(); i++) {
			Map<String, Object> map = genre_List.get(i);
			String gen1 = (String) map.get("genre");
			for (int j = i + 1; j < genre_List.size(); j++) {
				Map<String, Object> map2 = genre_List.get(j);
				String gen2 = (String) map2.get("genre");
				if (gen1.equals(gen2)) {
					genre_List.remove(j);
					j--;
				}
			}
		}
		//結果を3等分する
		int size = tests.size();
		int size2 = size / 3;
		int size3 = size - size2;
		List<Map<String, Object>> test1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> test2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> test3 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < size; i++) {
			if (i < size2) {
				test1.add(tests.get(i));
			} else if (i < size3) {
				test2.add(tests.get(i));
			} else {
				test3.add(tests.get(i));
			}
		}
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("test1", test1);
		model.addAttribute("test2", test2);
		model.addAttribute("test3", test3);
		model.addAttribute("genre", genre_List);
		model.addAttribute("tests", tests);
		return "teachertestmenu";
	}


	//生徒一覧画面
	@RequestMapping(path = "/teacherstumenu", method = RequestMethod.GET)
	public String stuMenuGet(HttpSession session, Model model, String selectclass) {
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

		return "teacherstumenu";
	}

	//生徒一覧から個人成績画面
	@RequestMapping(path = "/teacherstueval/{studentID}", method = RequestMethod.GET)
	public String stuMenuPOST(HttpSession session, Model model, @PathVariable String studentID) {
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT questionNumber,answer_rate,end_time FROM eval WHERE studentID = ? order by end_time asc", studentID);

		//resultの中身のend_timeを年月日:時分に変換する
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = result.get(i);
			String time = (String) map.get("end_time").toString();
			String year = time.substring(0, 4);
			String month = time.substring(5, 7);
			String day = time.substring(8, 10);
			String hour = time.substring(11, 13);
			String minute = time.substring(14, 16);
			String newTime = year + "/" + month + "/" + day + " " + hour + ":" + minute ;
			map.put("end_time", newTime);
		}
		model.addAttribute("test_list", result);

		return "teacherstueval";
	}

	//ここから問題編集

	@RequestMapping(path = "/testedit/{num}", method = RequestMethod.GET)
	public String testexGet(Model model,@PathVariable String num,HttpSession session) {

		/*セッションの中身がない場合、ログイン画面へ移行*/
		if (check.teacherSessionCheck(session)) {
			return "redirect:/sessionError";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> q_result;
		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> c_result;
		//SELECT文の結果をしまう
		String title = jdbcTemplate.queryForObject("select title from testtitle where questionNumber = ?",String.class,num);
		//SELECT文の結果をしまう
		String genre = jdbcTemplate.queryForObject("select genre from testtitle where questionNumber = ?",String.class,num);
		//SELECT文の実行
		q_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?",num);
		//SELECT文の実行
		c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",num);

		List<Integer> delNum  = new ArrayList<Integer>(); 
		List<Integer> delimg  = new ArrayList<Integer>();

		//choiceの数分のナンバリング
		for (int i = 0; i < c_result.size(); i++) {
			System.out.println(i);
			delNum.add(i, i+1);
		}
		//tetsの数分のナンバリング
		for (int i = 0; i < q_result.size(); i++) {
			System.out.println(i);
			delimg.add(i, i + 1);
		}

		//languageの一覧を取得
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select genre from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < n_result.size(); i++) {
			Map<String, Object> map = n_result.get(i);
			String lang = (String) map.get("genre");
			for (int j = i + 1; j < n_result.size(); j++) {
				Map<String, Object> map2 = n_result.get(j);
				String lang2 = (String) map2.get("genre");
				if (lang.equals(lang2)) {
					n_result.remove(j);
					j--;
				}
			}
		}
		model.addAttribute("delimg",delimg);
		model.addAttribute("imgsize",q_result.size());
		model.addAttribute("size", c_result.size());
		model.addAttribute("genre", genre);
		model.addAttribute("title", title);
		model.addAttribute("genre_list", n_result);
		model.addAttribute("delNum", delNum);
		model.addAttribute("image", q_result);
		model.addAttribute("number", num);
		model.addAttribute("question",c_result);

		return "testedit";
	}

	//問題タイトルの編集
	@RequestMapping(path = "/titleedit", method = RequestMethod.POST)
	public String edit_title(String title,String num) {
		jdbcTemplate.update("update testtitle set title = ? where questionNumber = ?",title,num);
		return "redirect:/testedit/" + num; 
	}

	//画像の編集
	@RequestMapping(path = "/imgedit", method = RequestMethod.POST)
	public String edit_image(MultipartFile upimage,String num,String imagenum,Model model)
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

			//DBに画面から入力されたデータを登録する。
			jdbcTemplate.update("insert into tests (questionNumber,image,imageNumber) value (?,?,?);"
					,num ,encodedImage,imageNumber);

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

	//ジャンルの編集

	@RequestMapping(path = "/genreedit", method = RequestMethod.POST)
	public String edit_lang(String genre, String num) {
		jdbcTemplate.update("update testtitle set genre = ? where questionNumber = ?", genre, num);
		return "redirect:/testedit/" + num;
	}

	//問題制作,編集
	@RequestMapping(path = "/questionedit", method = RequestMethod.POST)
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

	//ここまで問題編集

	//設問削除

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

	//ここまで問題削除

	//画像削除

	@RequestMapping(path = "/deleteimg", method = RequestMethod.POST)
	public String deleteimg(String quenum, String imagenum, HttpSession session)
			throws IOException {

		jdbcTemplate.update("delete from tests where questionNumber = ? and imageNumber = ?", quenum, imagenum);

		//今まであった画像の数をimageNumber昇順で取得
		List<Map<String, Object>> n_result = jdbcTemplate
				.queryForList("select * from tests where questionNumber = ? ORDER BY imageNumber asc", quenum);
		//サイズを取り出してナンバリングをする
		int size = n_result.size();
		//ナンバリング
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = n_result.get(i);
			int imageNumber = (int) map.get("imageNumber");
			jdbcTemplate.update("update tests set imageNumber = ? where questionNumber = ? and imageNumber = ?",
					i + 1, quenum, imageNumber);
		}
		return "redirect:/testedit/" + quenum;

	}

	//ここまで画像削除
	//問題新規作成

	@RequestMapping(path = "/newtest", method = RequestMethod.GET)
	public String newtestGet(HttpSession session,Model model) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}

		//languageの一覧を取得
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select genre from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < n_result.size(); i++) {
			Map<String, Object> map = n_result.get(i);
			String genre = (String) map.get("genre");
			for (int j = i + 1; j < n_result.size(); j++) {
				Map<String, Object> map2 = n_result.get(j);
				String genre2 = (String) map2.get("genre");
				if (genre.equals(genre2)) {
					n_result.remove(j);
					j--;
				}
			}
		}

		model.addAttribute("genre_list", n_result);
		return "newtest";
	}

	@RequestMapping(path = "/newtest", method = RequestMethod.POST)
	public String newtestGet(HttpSession session,Model model,String genre
			,String first,String second,String third,String forth,String answer_num,
			MultipartFile image,String title) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}
		String encodedImage = null;
		//受け取ったすべての変数が""ではないかつupimageが入っている場合のみ進む
		if(!genre.equals("") &&
				!first.equals("") && !second.equals("") && !third.equals("") && !forth.equals("") &&
				!answer_num.equals("") && !title.equals("") && image != null && !image.isEmpty()) {
			//アップロードされたファイルをバイトデータに変換する。
			try {
				byte[] byteData = image.getBytes();
				encodedImage = Base64.getEncoder().encodeToString(byteData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			//languageの一覧を取得
			List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select language from testtitle");

			//nresultのかぶった項目を取り除く
			for (int i = 0; i < n_result.size(); i++) {
				Map<String, Object> map = n_result.get(i);
				String gen = (String) map.get("genre");
				for (int j = i + 1; j < n_result.size(); j++) {
					Map<String, Object> map2 = n_result.get(j);
					String gen2 = (String) map2.get("genre");
					if (gen.equals(gen2)) {
						n_result.remove(j);
						j--;
					}
				}
			}
			//アラートで入力を求める
			model.addAttribute("null_error", "全ての項目を入力してください");
			model.addAttribute("genre", genre);
			model.addAttribute("first", first);
			model.addAttribute("second", second);
			model.addAttribute("third", third);
			model.addAttribute("forth", forth);
			model.addAttribute("answer_num", answer_num);
			model.addAttribute("image", image);
			model.addAttribute("title", title);
			model.addAttribute("genre_list", n_result);

			return "newtest";
		}
		model.addAttribute("genre", genre);
		model.addAttribute("first", first);
		model.addAttribute("second", second);
		model.addAttribute("third", third);
		model.addAttribute("forth", forth);
		model.addAttribute("answer_num", answer_num);
		model.addAttribute("title", title);
		model.addAttribute("encodedImage", encodedImage);
		return "/preview";
	}

	//プレビュー画面
	@RequestMapping(path = "/preview", method = RequestMethod.POST)
	public String preview(HttpSession session,Model model,String genre,String first,String second,String third,String forth,String answer_num,String title,String image) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
		if (check.teacherSessionCheck(session)) {

			// studentIDがある場合、studentmainへ移行
			if (session.getAttribute("studentID") != null) {
				return "redirect:/sessionErrorS";
			}

			return "redirect:/sessionError";
		}
		String[] questions = {first,second,third,forth};
		//選択肢の数値をint型に変換
		int ans = Integer.parseInt(answer_num);
		//選択肢の数値から答えを取得
		String answer = questions[ans - 1];
		//今まであった問題の数+1を取得
		int num = jdbcTemplate.queryForObject("select count(*) from testtitle", Integer.class) + 1;
		//タイトルの登録
		jdbcTemplate.update("insert into testtitle (questionNumber,title,genre) value (?,?,?)",num,title,genre);
		//新規問題の登録
		jdbcTemplate.update("insert into tests (questionNumber,image,imageNumber) value (?,?,1)",
				num, image);
		//新規選択肢の登録
		jdbcTemplate.update("insert into choices (questionNumber,select_first,select_sec,select_third,select_forth,answer,selectNumber) value (?,?,?,?,?,?,1)",
				num,first,second,third,forth,answer);

		return "redirect:/testedit/" + num;
	}

	//問題削除

	@RequestMapping(path = "/deletetest/{num}", method = RequestMethod.GET)
	public String deleteTest(@PathVariable String num) {
		System.out.println(num);
		jdbcTemplate.update("delete from testtitle where questionNumber = ?", num);
		jdbcTemplate.update("delete from tests where questionNumber = ?", num);
		jdbcTemplate.update("delete from choices where questionNumber = ?", num);
		//今まであった問題の数をselectNumber昇順で取得
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select * from testtitle ORDER BY questionNumber asc");
		//サイズを取り出してナンバリングをする
		int size = n_result.size();
		//ナンバリング
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = n_result.get(i);
			int questionNumber = (int) map.get("questionNumber");
			jdbcTemplate.update("update testtitle set questionNumber = ? where questionNumber = ?",
					i + 1, questionNumber);
		}
		//test,choicesのquestionNumberを1ずつ減らす
		jdbcTemplate.update("update tests set questionNumber = questionNumber - 1 where questionNumber > ?",num);
		jdbcTemplate.update("update choices set questionNumber = questionNumber - 1 where questionNumber > ?",num);

		return "redirect:/teachertestmenu";
	}
}
