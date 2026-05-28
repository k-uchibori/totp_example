package com.example.totp;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/home")
	public String home(HttpSession session) {
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login"; // ログインしていない場合はログイン画面にリダイレクト
		}
		return "home"; // ホーム画面のテンプレート名
	}

}
