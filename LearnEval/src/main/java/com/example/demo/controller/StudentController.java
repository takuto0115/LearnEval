package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

    SessionCheck check = new SessionCheck();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/studentmain", method = RequestMethod.GET)
    public String mainGet(HttpSession session) {
        System.out.println("studentmain");

        // studentIDがない場合、sessionErrorへ移行
        if (session.getAttribute("studentID") == null) {
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
        if (session.getAttribute("studentID") == null) {
            return "redirect:/sessionError";
        }

        // SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> tests;

        // SELECT文の実行
        tests = jdbcTemplate.queryForList("select * from tests");

        // questionNumberが被っているものを削除する
        for (int i = 0; i < tests.size(); i++) {
            Map<String, Object> map = tests.get(i);
            int questionNumber = (int) map.get("questionNumber");
            for (int j = i + 1; j < tests.size(); j++) {
                Map<String, Object> map2 = tests.get(j);
                int questionNumber2 = (int) map2.get("questionNumber");
                if (questionNumber == questionNumber2) {
                    tests.remove(j);
                    j--;
                }
            }
        }

        // 実行結果をmodelにしまってHTMLで出せるようにする。
        model.addAttribute("question", tests);

        return "studenttestmenu";
    }

    @RequestMapping(path = "/testpage/{num}", method = RequestMethod.GET)
    public String testexGet(Model model, @PathVariable String num, HttpSession session) {

        // studentIDがない場合、sessionErrorへ移行
        if (session.getAttribute("studentID") == null) {
            return "redirect:/sessionError";
        }

        session.setAttribute("qestion", num);

        // SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> q_result;

        // SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> c_result;

        // SELECT文の実行
        q_result = jdbcTemplate.queryForList("select * from tests where questionNumber = ?", num);

        // SELECT文の実行
        c_result = jdbcTemplate.queryForList("SELECT * FROM choices WHERE questionNumber = ? ORDER BY selectNumber asc",
                num);

        Map<String, Object> question = q_result.get(0);

        String image = (String) question.get("image");
        int number = ((Number) question.get("questionNumber")).intValue();

        model.addAttribute("image", image);
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

        List<Map<String, Object>> result;
        Map<String, Object> map;

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

        // ratioListをmodelに保存する
        model.addAttribute("ratioList", ratioList);

        // selectListをmodelに保存する
        model.addAttribute("selectList", selectList);

        // answer_rateをmodelに保存する
        model.addAttribute("answer_rate", answer_rate);

        // 終了時刻をmodelに保存する
        model.addAttribute("endTime", endTime);

        return "testeval";

    }

    @RequestMapping(path = "/studenteval", method = RequestMethod.GET)
    public String evalGet(HttpSession session) {
        /* セッションの中身がない場合、ログイン画面へ移行 */
        // studentIDがない場合、sessionErrorへ移行
        if (session.getAttribute("studentID") == null) {
            return "redirect:/sessionError";
        }
        return "studenteval";
    }

    @RequestMapping(path = "/studenteval", method = RequestMethod.POST)
    public String evalPOST() {
        return "redirect:/studentmain";
    }

}
