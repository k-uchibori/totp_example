package com.example.totp;

import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import dev.samstevens.totp.secret.DefaultSecretGenerator;

public class QRCodeUtil {
	
	/**
	 * TOTP secret key を生成する
	 * @return
	 */
	public String generateSecret() {
		// TOTP secret key を生成するロジックを実装
		DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
		return secretGenerator.generate();	// Base32 形式のシークレットキーを生成
	}
	
	/**
	 * QRコードのURLを生成する
	 * @param username
	 * @param secret
	 * @return
	 */
	public String generateQRCodeURL(String username, String secret) {
		// QRコードのURLを生成するロジックを実装
		String issuer = "MyApp"; // アプリ名などの発行者情報
		return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, username, secret, issuer);
	}
	
	/**
	 * QRコード画像作成
	 */
	public byte[] generateQRCode(String url) throws Exception {
		// QRコード画像を生成するロジックを実装
		// ここでは ZXing ライブラリを使用して QRコードを生成する
		int width = 300;
		int height = 300;
		MultiFormatWriter qrCodeWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
		
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		return pngOutputStream.toByteArray();
	}

}
