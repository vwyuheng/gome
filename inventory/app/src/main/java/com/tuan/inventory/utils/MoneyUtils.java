package com.tuan.inventory.utils;

import java.math.BigDecimal;

public class MoneyUtils {
	/**
	 * 元转换成分
	 * @param money 金额：元
	 * @return 金额：分
	 */
	public static String yuan2Fen(String money){
		if(StringUtils.isEmpty(money)){
			return "";
		}
		BigDecimal yuan = new BigDecimal(money);
		return yuan.multiply(new BigDecimal(100)).toString();
	}
	
	/**
	 * 负数转正数
	 * @return
	 */
	public static String negative2Positive(String money){
		if(StringUtils.isEmpty(money)){
			return "";
		}
		BigDecimal yuan = new BigDecimal(money);
		if(yuan.compareTo(new BigDecimal(0)) < 0){
			return yuan.multiply(new BigDecimal("-1")).toString();
		}
		return yuan.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(negative2Positive("-10.123"));
		System.out.println(negative2Positive("10.123"));
		System.out.println(negative2Positive("-10.00"));
		System.out.println(negative2Positive("-10"));
		System.out.println(negative2Positive("0"));
		System.out.println(yuan2Fen("0"));
		System.out.println(yuan2Fen("1.23"));
		System.out.println(yuan2Fen("123"));
		System.out.println(yuan2Fen("0.12"));
		System.out.println(yuan2Fen("-123"));
		System.out.println(yuan2Fen("-1.23"));
	}
}
