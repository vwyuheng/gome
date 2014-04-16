package com.tuan.inventory.utils;

import java.security.Key;
import java.security.Security;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class JeeheDES {

	private static final String strDefaultKey = "L6Xe8dXVGISZ17LJy7GzZaeYGpeGfe";

	private Cipher encryptCipher = null;

	private Cipher decryptCipher = null;

	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public JeeheDES() {
		this(strDefaultKey);
	}

	@SuppressWarnings("restriction")
	public JeeheDES(String strKey) {

		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		try {
			Key key = getKey(strKey.getBytes());
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public byte[] encrypt(byte[] arrB) {
		try {
			return encryptCipher.doFinal(arrB);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();

			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();

			return null;
		}
	}

	public String encrypt(String strIn) {
		try {
			return byteArr2HexStr(encrypt(strIn.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	public String decrypt(String strIn) {

		if (strIn == null) {
			return null;
		}
		try {
			return new String(decrypt(hexStr2ByteArr(strIn)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Key getKey(byte[] arrBTmp) throws Exception {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];

		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}

		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

		return key;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String test = "503329";// 183837 332589,199902,199831,516665
			// c229516fd63407a1
			// ,0914bf2af8c0936d,45059178bacf8f6c,891bb98eaee8bef2
			// DESPlus des = new DESPlus();//默认密钥
			// JeeheDES des = new JeeheDES("leemenzgggg");// 自定义密钥
			JeeheDES des = new JeeheDES();
			System.out.println("加密前的字符：" + test);
			System.out.println("加密后的字符：" + des.encrypt(test));
			System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
			System.out.println("解密后的字符：" + des.decrypt("2910edcfd0c1d463"));

			System.out.println(new Date(1334601118000L));
			System.out.println(new Date(1334601034000L));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
