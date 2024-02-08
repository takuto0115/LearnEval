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
		System.out.println("teachermain");
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

		//SELECT文の実行
		tests = jdbcTemplate.queryForList("select * from testtitle");

		String newNum = Integer.toString(tests.size() + 1);
		System.out.println(newNum);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("question", tests);
		model.addAttribute("newNum", newNum);


		return "teachertestmenu";
	}


	@RequestMapping(path = "/teacherstumenu", method = RequestMethod.GET)
	public String stuMenuGet(HttpSession session) {
		/*セッションの中身がない場合、ログイン画面へ移行*/
		// teacherIDがない場合、sessionErrorへ移行
        if (check.teacherSessionCheck(session)) {

            // studentIDがある場合、studentmainへ移行
            if (session.getAttribute("studentID") != null) {
                return "redirect:/sessionErrorS";
            }

            return "redirect:/sessionError";
        }
        
        //studentの一覧を取得
        List<Map<String, Object>> s_result = jdbcTemplate.queryForList("select * from students");
        
        
        System.out.println(s_result);
        //studentの一覧をmodelにしまってHTMLで出せるようにする。
        session.setAttribute("student", s_result);
        
		return "teacherstumenu";
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
		String language = jdbcTemplate.queryForObject("select language from testtitle where questionNumber = ?",String.class,num);
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

		//languageの一覧を取得
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select language from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < n_result.size(); i++) {
			Map<String, Object> map = n_result.get(i);
			String lang = (String) map.get("language");
			for (int j = i + 1; j < n_result.size(); j++) {
				Map<String, Object> map2 = n_result.get(j);
				String lang2 = (String) map2.get("language");
				if (lang.equals(lang2)) {
					n_result.remove(j);
					j--;
				}
			}
		}

		model.addAttribute("size", c_result.size());
		model.addAttribute("language", language);
		model.addAttribute("title", title);
		model.addAttribute("lang_list", n_result);
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

	//問題制作,編集
	@RequestMapping(path = "/questionedit", method = RequestMethod.POST)
	public String edit_q(String first,String second,String third,String forth,String answer_num,String quenum,String num,HttpSession session)
			throws IOException {
		System.out.println(first + second + third + forth);
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
	
	//問題削除

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
	
	//新規問題作成

	@RequestMapping(path = "/newtest/{num}", method = RequestMethod.GET)
	public String newtestGet(HttpSession session,@PathVariable String num,Model model) {
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
		List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select language from testtitle");

		//nresultのかぶった項目を取り除く
		for (int i = 0; i < n_result.size(); i++) {
			Map<String, Object> map = n_result.get(i);
			String language = (String) map.get("language");
			for (int j = i + 1; j < n_result.size(); j++) {
				Map<String, Object> map2 = n_result.get(j);
				String language2 = (String) map2.get("language");
				if (language.equals(language2)) {
					n_result.remove(j);
					j--;
				}
			}
		}

		model.addAttribute("lang_list", n_result);
		model.addAttribute("num", num);
		return "newtest";
	}

	@RequestMapping(path = "/newtest", method = RequestMethod.POST)
	public String newtestGet(HttpSession session,Model model,String num,String language
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
		if(!num.equals("") && !language.equals("") &&
				!first.equals("") && !second.equals("") && !third.equals("") && !forth.equals("") &&
				!answer_num.equals("") && !title.equals("") && image != null && !image.isEmpty()) {
			//アップロードされたファイルをバイトデータに変換する。
			try {
				byte[] byteData = image.getBytes();
				encodedImage = Base64.getEncoder().encodeToString(byteData);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] questions = {first,second,third,forth};
			//選択肢の数値をint型に変換
			int ans = Integer.parseInt(answer_num);
			//選択肢の数値から答えを取得
			String answer = questions[ans - 1];
			//タイトルの登録
			jdbcTemplate.update("insert into testtitle (questionNumber,title,language) value (?,?,?)",num,title,language);
			//新規問題の登録
			jdbcTemplate.update("insert into tests (questionNumber,image,imageNumber) value (?,?,1)",
					num, encodedImage);
			//新規選択肢の登録
			jdbcTemplate.update("insert into choices (questionNumber,select_first,select_sec,select_third,select_forth,answer,selectNumber) value (?,?,?,?,?,?,1)",
					num,first,second,third,forth,answer);
		}else {
			//languageの一覧を取得
			List<Map<String, Object>> n_result = jdbcTemplate.queryForList("select language from testtitle");

			//nresultのかぶった項目を取り除く
			for (int i = 0; i < n_result.size(); i++) {
				Map<String, Object> map = n_result.get(i);
				String lang = (String) map.get("language");
				for (int j = i + 1; j < n_result.size(); j++) {
					Map<String, Object> map2 = n_result.get(j);
					String lang2 = (String) map2.get("language");
					if (lang.equals(lang2)) {
						n_result.remove(j);
						j--;
					}
				}
			}
			//アラートで入力を求める
			model.addAttribute("null_error", "全ての項目を入力してください");
			//受け取った変数をすべてmodelに格納
			model.addAttribute("num", num);
			model.addAttribute("language", language);
			model.addAttribute("first", first);
			model.addAttribute("second", second);
			model.addAttribute("third", third);
			model.addAttribute("forth", forth);
			model.addAttribute("answer_num", answer_num);
			model.addAttribute("image", image);
			model.addAttribute("title", title);
			model.addAttribute("lang_list", n_result);

			return "newtest";
		}
		return "testedit/" + num;
	}
	
	//ここまで新規問題作成
}
