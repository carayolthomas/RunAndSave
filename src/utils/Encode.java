package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class allow to encode password giving by the User (security is important
 * !)
 * 
 * @author hti
 * 
 */
public class Encode {

	public static String encode(String pPassword, String pAlgorithm)
			throws NoSuchAlgorithmException {
		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance(pAlgorithm);
			hash = md.digest(pPassword.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hash.length; ++i) {
			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				sb.append(0);
				sb.append(hex.charAt(hex.length() - 1));
			} else {
				sb.append(hex.substring(hex.length() - 2));
			}
		}
		return sb.toString();
	}
}

