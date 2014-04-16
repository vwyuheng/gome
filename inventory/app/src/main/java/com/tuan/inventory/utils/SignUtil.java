package com.tuan.inventory.utils;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Cipher;

/**
 * 数据签名工具类
 * @author tianzq
 * @date 2013.11.27
 */
public class SignUtil {
	
	/**
	 * 生成RSA密钥对
	 * @return 密钥对java.util.HsahMap对象
	 */
	public static Map<String, String> genKeyPair() {
		Map<String, String> map = null;
		try {
			map = new HashMap<String, String>();
			KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
			keyPair.initialize(1024);
			KeyPair kp = keyPair.generateKeyPair();
			String pubKeyStr = byteArr2HexString(kp.getPublic().getEncoded());
			String priKeyStr = byteArr2HexString(kp.getPrivate().getEncoded());
			
			map.put("publicKey", pubKeyStr);
			map.put("privateKey", priKeyStr);
		} catch (Exception e) {
			e.printStackTrace();
			map = null;
		}
		return map;
	}
	
	/**
	 * 私钥签名数据
	 * @param data 需要签名的数据，格式为key-value字符串，如name=zhangsan&age=24
	 * @param privateKey 私钥
	 * @return 签名结果
	 */
	private static String sign(String data, String privateKey) {
		String sign = null;
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(hexString2ByteArr(privateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyFactory.generatePrivate(keySpec);
			Signature si = Signature.getInstance("SHA1WithRSA");
			si.initSign(priKey);
			si.update(sort(data).getBytes("UTF-8"));
			byte[] dataSign = si.sign();
			sign =  byteArr2HexString(dataSign);
		} catch (Exception e) {
			e.printStackTrace();
			sign = null;
		}
		return sign;
	}

	/**
	 * 公钥验签数据
	 * @param data 需要验签的数据，格式为key-value字符串，如name=zhangsan&age=24
	 * @param sign 签名
	 * @param publicKey 公钥
	 * @return true/false
	 */
	public static boolean verify(String data, String sign, String publicKey) {
		boolean succ = false;
		try {
			Signature verf = Signature.getInstance("SHA1WithRSA");
			KeyFactory keyFac = KeyFactory.getInstance("RSA");
			PublicKey puk = keyFac.generatePublic(new X509EncodedKeySpec(hexString2ByteArr(publicKey)));
			verf.initVerify(puk);
			verf.update(sort(data).getBytes("UTF-8"));
			succ = verf.verify(hexString2ByteArr(sign));
		} catch (Exception e) {
			e.printStackTrace();
			succ = false;
		}
		return succ;
	}
	
	/**
	 * 公钥加密数据
	 * @param data 需要加密数据
	 * @param publicKey 公钥
	 * @return 加密数据
	 */
	public static String encrypt(String data, String publicKey) {
		String encryptData = null;
		try {
			KeyFactory keyFac = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFac.generatePublic(new X509EncodedKeySpec(hexString2ByteArr(publicKey)));
			
			Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] result = cipher.doFinal(data.getBytes("UTF-8"));
			encryptData = byteArr2HexString(result);
		} catch (Exception e) {
			e.printStackTrace();
			encryptData = null;
		}
		return encryptData;
	}
	
	/**
	 * 私钥解密数据
	 * @param encryptedData 已加密数据
	 * @param privateKey 私钥
	 * @return 解密数据
	 */
	public static String decrypt(String encryptedData, String privateKey) {
		String decryptData = null;
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(hexString2ByteArr(privateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyFactory.generatePrivate(keySpec);
			
			Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] result = cipher.doFinal(hexString2ByteArr(encryptedData));
			decryptData = new String(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			decryptData = null;
		}
		return decryptData;
	}
	
	/**
	 * 对外（银商）的参数串排序方法
	 * @param data
	 * @return
	 */
	private static String sort(String data) {
		TreeMap<String,String> dataMap = new TreeMap<String,String>();
		String[] params = data.split("&");
		for (int i=0; i<params.length; i++) {
			String[] param = params[i].split("=");
			String name = "", value = "";
			if (param.length == 2) {
				name = param[0];
				value = param[1];
			} else if (param.length == 1) {
				name = param[0];
			}
			dataMap.put(name, value);
		}
		StringBuffer paramsStr = new StringBuffer();
		for(Entry<String,String> entry : dataMap.entrySet()){
			paramsStr.append(entry.getKey()).append("=").append(entry.getValue()==null?"":entry.getValue()).append("&");
		}
		paramsStr.deleteCharAt(paramsStr.length()-1);
		return paramsStr.toString();
	}
	
	/**
	 * 字节数组转换为十六进制字符串
	 * @param bytearr 字节数组
	 * @return 十六进制字符串
	 */
	public static String byteArr2HexString(byte[] bytearr) {
		if (bytearr == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();

		for (int k = 0; k < bytearr.length; k++) {
			if ((bytearr[k] & 0xFF) < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(bytearr[k] & 0xFF, 16));
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转换为字节数组
	 * @param hexString 16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexString2ByteArr(String hexString) {
		if ((hexString == null) || (hexString.length() % 2 != 0)) {
			return new byte[0];
		}

		byte[] dest = new byte[hexString.length() / 2];

		for (int i = 0; i < dest.length; i++) {
			String val = hexString.substring(2 * i, 2 * i + 2);
			dest[i] = (byte) Integer.parseInt(val, 16);
		}
		return dest;
	}
	
	/**
	 * 窝窝团内部系统之间的签名,
	 * 规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * @param parameters	参数SortedMap,包含wowo内部调用传来的sign属性
	 * @param key			签名加密KEY
	 * @return boolean		是否匹配
	 */
	public static boolean checkWowoSign(SortedMap<String,String> parameterMap) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = parameterMap.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		Entry<String, String> entry = null;
		while(it.hasNext()) {
			entry = (Entry<String, String>)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		
		//算出摘要
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toLowerCase();
		String tenpaySign = parameterMap.get("sign").toLowerCase();
		if(tenpaySign == null){
			return false;
		}
		return tenpaySign.equals(sign);
	}
	
	
	
	/**
	 * 创建签名数据。必须保证传入的map为TreeMap
	 * @param parameterMap	TreeMap
	 * @param privateKey	加密私钥
	 * @return	签名数据
	 */
	public static String makeSign(SortedMap<String,String> parameterMap,String privateKey){
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = parameterMap.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		Entry<String, String> entry = null;
		while(it.hasNext()) {
			entry = (Entry<String, String>)it.next();
			String k = (String)entry.getKey();
			if(k.equalsIgnoreCase("sign")){
				continue;
			}
			String v = (String)entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		if(sb.length() > 0){
			return sign(sb.substring(0, sb.length() - 1), privateKey);
		}
		return "";
	}
	
	public static void main(String[] args){
		String privateKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100a8143bd2d265a3701c9c10f71e9baa6a4ea1dd772a882ca8dacbd8012a83d3a43ab091572211378881025ca092eebe38cffb7a7269d9f1aac545dad4f50b5ad162cca78d64d3649125558fbd711d4c2303f3d4a20099bb0fdd790eba2da6d4e52477d8c76471057921d82a2ffdcba493967a5b93c175b76cd9c3caa6869e87670203010001028180708372f492d1651224a4b89aa48bb20d8debbf14098db4cfa7ffb2ee3ce69863a4c213d90f3e153db496bf28e2931d156cecbc3020f9bb5404d0d9479b6de9718c4da4b602adacf19eef3fa75d524b4f9208a2b53f31f07c3173673f5f486f41d0ca0974c9704fd7b88556f94e9d1846f3b15407edcde20ec8e6dd38024bbe29024100fbcaefb96df02e91f4812f8c1f1c9ddea2966d21f1762254c74be741934c3fd463ac3ff35b1aebff929bfc9383a5883a624c26c67138f0af22b9615f7cd12563024100aae3348c33220631421bd0536a834b4ea5ff93083ce478e902bf4a02f161ea021faf0e1e5df40aaa28d649088817c62933f208cbecf377e882e18d453279c72d02403b0c2f45b403745864177b98079fc561f58d0350c77865baadf61de861ebaab85b1b84efc3f8bf49730f439f306b3c543cc31100ce8284e6fd64adf21faa48730241009c5070c4be112570f173d7c035f21b74b9cb6a87628beb3dd9517935efef0ccb1c885875d6a3610dfd84e61173cbab6c0d241c7fcd23784fab48fe9c896b739102402abd15db26608bd877ffd08a445128faee6fae0b85dfd1fad62cb59f156b338d3f01bca20146e5f4ca998c4bc39161985d30464f14bdb0b1c37eedae975e4c29";
		//String publicKey = "30819f300d06092a864886f70d010101050003818d0030818902818100bb9efe4a66900babf8d1cae73842a8cc4525b2c6947880769278f27086850522c0c05d4e52b88cb6d33dbc1ec4ab7be88d0c22d81cef4aca2f6d91759827ca300a2b379fcc6568976c73af8f0c48b686c4afba2dbfe909ae717210421ccdb5897d1ec1dbf8253574d6537611a0612181c9b3cd58b163e8f84b53ae3115879f5f0203010001";
		//String privateKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100bb9efe4a66900babf8d1cae73842a8cc4525b2c6947880769278f27086850522c0c05d4e52b88cb6d33dbc1ec4ab7be88d0c22d81cef4aca2f6d91759827ca300a2b379fcc6568976c73af8f0c48b686c4afba2dbfe909ae717210421ccdb5897d1ec1dbf8253574d6537611a0612181c9b3cd58b163e8f84b53ae3115879f5f02030100010281801ec508773332d47733ab54576f469f6040c6d9f6ef4a83e6ee469f9c0cb3ac0ff3049948cb1031239aa9393ce28c2d1a0a67bf1f2a3fa4485c56dfd02550013c6fc54211731adf7a3571bdc0069b8e39d4d02b56c370ac7f6a8c87fa6527bfcbda29eeec864d7bab0a9efdb026277a7e8673aec8c12a1b3ef8ef0f98f7d8c6e1024100e12395d80c3eaec9259beb924730c1c88f022bfa098e2bec12e356e3997a071228937f175998f4a919a19a55abc79a2cc66c6d3502375b82e26589e244018105024100d556db120937e8a792df4fb629e727f796553bb8751b2135f578d8a4fd090f4606f2e47be57a949fc2ef3506a7577d170ffe2d0ff6c06846b8b895df8d5c9c1302402298cc5309f6abf75f1f29c12dcd7149f7a7f6c812a5d55c0ebefec034fa8a91a0c8a1b41de1a25f46000e6e71da5777dffa13821cd812e8f70f7173e2cd745d0240330c7f8b474d6b8c7da695ea5f088315c99147a311d29615d5513f75eb5e799b261a84f5e8a9765aec485505ec9110cbadf6024e699b0cd8af66ebe5fc6b86a5024100bf2e3e4b19a2cd45c72929a2beee7a6883d6ce18d97eceec58cbe134169b44325c122dc9a2e0498226afa649a36ea1118a17eedbb391b296770abcd48bba92f5";
		String publicKey = "30819f300d06092a864886f70d010101050003818d0030818902818100a8143bd2d265a3701c9c10f71e9baa6a4ea1dd772a882ca8dacbd8012a83d3a43ab091572211378881025ca092eebe38cffb7a7269d9f1aac545dad4f50b5ad162cca78d64d3649125558fbd711d4c2303f3d4a20099bb0fdd790eba2da6d4e52477d8c76471057921d82a2ffdcba493967a5b93c175b76cd9c3caa6869e87670203010001";
		//System.out.println(decrypt(encrypt("原始数据", publicKey), privateKey));
//		Resp resp = new Resp();
//		resp.setMsg_type("00");
//		resp.setMsg_txn_code("002102");
//		resp.setMsg_crrltn_id("20131227132542043088");
//		resp.setMsg_flg("1");
//		resp.setMsg_sender("33");
//		resp.setMsg_time("20131227133216");
//		resp.setMsg_sys_sn("20131227132542043088");
//		resp.setMsg_ver("0.1");
//		resp.setMsg_rsp_code("0000");
//		resp.setMsg_rsp_desc("成功");
//		SortedMap<String, String> reqMap = new TreeMap<String, String>();
//		resp.addHeadParameMap4Sign(reqMap, resp);
		
//		String publicKe = "30819f300d06092a864886f70d010101050003818d0030818902818100a8143bd2d265a3701c9c10f71e9baa6a4ea1dd772a882ca8dacbd8012a83d3a43ab091572211378881025ca092eebe38cffb7a7269d9f1aac545dad4f50b5ad162cca78d64d3649125558fbd711d4c2303f3d4a20099bb0fdd790eba2da6d4e52477d8c76471057921d82a2ffdcba493967a5b93c175b76cd9c3caa6869e87670203010001";
//		String sign = "008ec4d78800871c0be47855c7c90abb14ce7e0ffce818d2b3ef34ef55ddcceb566bf099d2f226e0555883990bb032e3e087a1287d4bde90c4291affc212541c2c22bd4831953adf6745339ebe89119840019fd4a22f6d147fd4a4c23081a2daae6401866577da5dfb08bd34605a9b0f891ebc3aa5712b71034ddce81b668313";
//		String data = "msg_crrltn_id=20131227152230043134&msg_flg=1&msg_rsp_code=0000&msg_rsp_desc=成功" +
//				"&msg_sender=33&msg_sys_sn=20131227152230043134&msg_time=20131227152904&msg_txn_code=002102" +
//				"&msg_type=00&msg_ver=0.1";
//		System.out.println("verify:" + SignUtil.verify(data, sign, publicKey));
		SortedMap<String, String> parameterMap = new TreeMap<String, String>();
		
		/*resp.setSyskey("22");
		resp.setMercBilfeeAmt("100.00");
		resp.setPassword("password");
		resp.setUserid("user2test");
		resp.setReceived("100.00");
		resp.setTransactionid("111");
		resp.setShopcode("22");
		resp.setTerminalcode("99");
		resp.setTransactiondt("1111111");
		
		*/
		
		parameterMap.put("datatype", "json");
		parameterMap.put("syskey", "suixingpay");
		
		parameterMap.put("reversalamount", "100");
		parameterMap.put("mercBilfeeAmt", "50");
		parameterMap.put("shopcode", "21322");
		parameterMap.put("terminalcode", "we22");
		parameterMap.put("transactionid", "20131227132523043087");
		parameterMap.put("oldtransactionid", "20131227132523043087");
		parameterMap.put("transactiondt", "1388121908");
		
		String sign = SignUtil.makeSign(parameterMap,privateKey);
		System.out.println(sign);
		//parameterMap.put("sign", "ce14e96452c601feabcd0ee9182063703e9c265a6a5fbabae103577eb4f417d50d22f3407a7414452ef388927f879b46a54918c1db8c71ad3ed9b9b7dae8543602bc5ee165dcdce942a2bee75615554a9eacfd7638f3c7ca7616a287af8a5683bf17b3a9b0a14873c342f998270c83460d58b133fdf9f8535f2331a99b0349e5");
		//String sign = parameterMap.get("sign");
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = parameterMap.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		Entry<String, String> entry = null;
		while(it.hasNext()) {
			entry = (Entry<String, String>)it.next();
			String k = (String)entry.getKey();
			if(k.equalsIgnoreCase("sign")){
				continue;
			}
			String v = (String)entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		System.out.println(sb.substring(0, sb.length() - 1));
		System.out.println(verify(sb.substring(0, sb.length() - 1), sign, publicKey));
		/*System.out.println(SignUtil.genKeyPair());*/
	}
}
