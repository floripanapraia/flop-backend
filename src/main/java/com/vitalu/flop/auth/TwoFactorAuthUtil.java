package com.vitalu.flop.auth;

import com.warrenstrange.googleauth.*;

public class TwoFactorAuthUtil {
	private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

	public static String generateSecretKey() {
		return gAuth.createCredentials().getKey();
	}

	public static boolean verifyCode(String secret, int code) {
		return gAuth.authorize(secret, code);
	}
}
