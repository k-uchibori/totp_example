package com.example.totp;

import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	/**
	 * ユーザを保存する
	 * @param user
	 */
	public void saveUser(User user) {

	}
	
	/**
	 * ユーザ名でユーザを検索する
	 * @param username
	 * @return
	 */
	public User findByUsername(String username) {
		// ユーザ名でユーザを検索するロジックを実装
		User user = new User();
		user.username = username;
		user.password = "password"; // 仮のパスワード
		user.mfaEnabled = false; // MFAが有効なユーザ
		return user; // 仮の戻り値
	}

}
