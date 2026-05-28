package com.example.totp;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	private final UserService userService;
	
	public LoginController(UserService userService) {
		this.userService = userService;
	}
	
	
	@GetMapping("/login")
	public String loginForm() {
		return "login"; // ログイン画面のテンプレート名
	}
	
	/**
	 * ログイン処理
	 * @param username
	 * @param password
	 * @param model
	 * @param session
	 * @return
	 */
	@PostMapping("/login")	
	public String login(@RequestParam String username, @RequestParam String password, Model model,HttpSession session) {
		// ユーザ認証のロジックを実装
		User user = userService.findByUsername(username);
		// 簡易的に、ログイン済みユーザの情報がある場合とで処理を分ける
		User logedInUser = (User) session.getAttribute("loginUser");
		if (logedInUser != null) {
			// 既にログインしているユーザの情報がある場合は、コード入力画面にリダイレクト
			session.setAttribute("user", logedInUser);
			
			return "mfa"; // TOTPコード入力画面のテンプレート
		}
		
		if (user != null && user.getPassword().equals(password)) {
			session.setAttribute("username", username); // ユーザ名をセッションに
			if (user.isMfaEnabled()) {
				// MFAが有効な場合、TOTPコードの入力画面にリダイレクト
				model.addAttribute("username", username);
				return "mfa"; // TOTPコード入力画面のテンプレート名
			} else {
				// MFAが無効な場合、MFA設定画面にリダイレクト
				return "setup"; // 設定画面に遷移
			}
		} else {
			// 認証失敗の処理を実装
			model.addAttribute("error", "Invalid username or password");
			return "login"; // ログイン画面のテンプレート名
		}
	}
	
	/**
	 * QRコードの生成と表示
	 * @param model
	 * @return
	 */
	@GetMapping("/qrCode")
	public ResponseEntity<byte[]> qrCode(HttpSession session) throws Exception {
		String username = (String) session.getAttribute("username");
		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		User user = userService.findByUsername(username);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		QRCodeUtil qrCodeUtil = new QRCodeUtil();	
		// 秘密鍵の生成
		String secret = qrCodeUtil.generateSecret();
		user.setSecret(secret);
		user.setMfaEnabled(true);
		
		// セッションにユーザ情報を保存する
		session.setAttribute("user", user);
		
		// QRコードのURLを生成
		String url = qrCodeUtil.generateQRCodeURL(user.username, user.secret);
		byte[] qrCodeImage = qrCodeUtil.generateQRCode(url);
		
		return ResponseEntity.ok()
				.header("Content-Type", "image/png")
				.body(qrCodeImage);
	}
	
	/**
	 * TOTPコードの検証
	 * @param code
	 * @param session
	 * @return
	 */
	@PostMapping("/mfa")
	public String mfa(String code, HttpSession session,RedirectAttributes model) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login"; // ログイン画面にリダイレクト
		}
		
		TotpUtil totpUtil = new TotpUtil();
		if (totpUtil.verifyTotpCode(user.getSecret(), code)) {
			// TOTPコードが正しい場合、ログイン成功の処理を実装
			session.removeAttribute("user"); // セッションからユーザ情報を削除
			session.setAttribute("loginUser", user);
			return "redirect:/home"; // ホーム画面に遷移
		} else {
			// TOTPコードが間違っている場合、エラーメッセージを表示
			session.removeAttribute("user"); // セッションからユーザ情報を削除
			model.addFlashAttribute("error", "Invalid TOTP code" + code);
			return "redirect:/login"; // ログイン画面にリダイレクト
		}
	}
	
	/**
	 * ログアウト処理
	 * @param session
	 * @return
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // セッションを無効化してログアウト
		return "redirect:/login"; // ログイン画面にリダイレクト
	}
	
}
