package com.example.totp;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class TotpUtil {
	
	/**
	 * TOTPコードを検証する
	 * @param secret
	 * @param code
	 * @return
	 */
	public boolean verifyTotpCode(String secret, String code) {
		// TOTPコードを検証するロジックを実装
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		
		// CodeVerifier を使用して TOTPコードを検証
		CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		
		// シークレットキーとユーザーが入力したコードを検証
		return verifier.isValidCode(secret, code);
		
	}

}
