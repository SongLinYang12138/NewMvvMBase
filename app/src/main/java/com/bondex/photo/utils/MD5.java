package com.bondex.photo.utils;

import com.bondex.library.util.CommonUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public final static String to_MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',	'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes("utf-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	public static void main(String args[]) {
		System.out.print(MD5.to_MD5("test"));
	}



	public static String getFileMD5(String path) {
		if (CommonUtils.isEmpty(path)) {
			return null;
		}
		MessageDigest digest = null;
		byte buffer[] = new byte[1024];
		int len;


		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			digest = MessageDigest.getInstance("MD5");

			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
			return new String(encodeHex(digest.digest()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;

		}

	}
	public static char[] encodeHex(byte[] data) {
//		DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//		DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//
		final char[] toDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

		int l = data.length;
		char[] out = new char[l << 1];
		int i = 0;

		for (int j = 0; i < l; ++i) {
			out[j++] = toDigits[(240 & data[i]) >>> 4];
			out[j++] = toDigits[15 & data[i]];
		}

		return out;
	}
}