package com.tuan.inventory.resp.inner;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @description
 * 卡签名报文
 * @author tianzq
 * @date 2013.11.20
 */
@XmlRootElement(name = "resp")
public class CardSignResp extends InnerResp {
	private static final long serialVersionUID = -2579884095088985699L;
	/**
	 * 银商加密后卡号
	 */
	private String encCardNo;
	/**
	 * 卡类型名称
	 */
	private String cardTypeName;
	/**
	 * 发卡行名称
	 */
	private String bankName;
	
	public String getEncCardNo() {
		return encCardNo;
	}
	public void setEncCardNo(String encCardNo) {
		this.encCardNo = encCardNo;
	}
	public String getCardTypeName() {
		return cardTypeName;
	}
	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Override
	public String toString() {
		return "ResignResp [encCardNo=" + encCardNo + ", cardTypeName="
				+ cardTypeName + ", bankName=" + bankName + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
