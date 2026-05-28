package com.example.totp;

/**
 * ユーザ
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class User {
	String username;
	String password;
	String secret;			// TOTP secret key
	boolean mfaEnabled;	
}
