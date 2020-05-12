package com.scyh.applock.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;



/**
 * DES加密解密算法
 * 
 * @author fxq33
 *
 */
public class DESHelper {

	/**
	 * 加密
	 * 
	 * @param data
	 * @param iv
	 * @return
	 */
	public static String encryptBasedDes(String data, String iv) {
		String encryptedData = null;
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(iv.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			// 加密对象
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key, sr);
			// 加密，并把字节数组编码成字符串
//			encryptedData =Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.DEFAULT);
			return Base64Helper.encode(cipher.doFinal(data.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
	}

	/**
	 * 解密
	 * 
	 * @param cryptData
	 * @param iv
	 * @return
	 */
	public static String decryptBasedDes(String cryptData, String iv) {
		String decryptedData = null;
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(iv.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			// 解密对象
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key, sr);
			// 把字符串解码为字节数组，并解密
//			decryptedData = new String(cipher.doFinal(Base64.decode(cryptData.getBytes(), Base64.DEFAULT)));
//			decryptedData=Base64Helper.a(cipher.doFinal(Base64Helper.a(cryptData)));
			return new String(cipher.doFinal(Base64Helper.decode(cryptData)));
			//System.out.println("解密："+decryptedData);
		//	return new String(cipher.doFinal(Base64.decode(cryptData.getBytes())));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}
	
	public static void main(String[] args) {
		String iv="123kk!@#";
		String data="今天感觉还不错！23";
		String encrypt=encryptBasedDes(data, iv);
		System.err.println("----------------加密----------------");
		System.out.println(encrypt);
		
		String decrypt=decryptBasedDes(encrypt, iv);
		System.out.println("---------------解密----------------");
		System.out.println(decrypt);
	}

}
