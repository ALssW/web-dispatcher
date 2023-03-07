package com.alva.utils;

import java.security.MessageDigest;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-15
 */
public class Md5Util {

	public static String getMd5(String message) {
		String md5str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] buff = md.digest(input);
			md5str = bytesToHex(buff);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5str;
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuilder md5str = new StringBuilder();
		// 把数组每一字节换成16进制连成md5字符串
		int digital;
		for (byte aByte : bytes) {
			digital = aByte;

			if (digital < 0) {
				digital += 256;
			}
			if (digital < 16) {
				md5str.append("0");
			}
			md5str.append(Integer.toHexString(digital));
		}
		return md5str.toString().toUpperCase();
	}

}
