package com.gome.model.result;

import com.gome.core.common.lang.GomeBaseDO;

/**
 * 结果数据 
 * 		是否成功 --不代表业务结果
 *      系统错误枚举
 *      业务结果对象    包含具体的业务枚举
 *      业务附件对象（一般是异常对象或者其他信息）
 * @param <T extends Serializable > 
 */
public class CallResult<T> extends GomeBaseDO {
	private static final long serialVersionUID = 8136212258133597383L;
	
	/** 返回码 **/
	public int code;
	/** 返回描述（英文） **/
	public String description;
	/** 返回对象 **/
	public Object businessResult;
	
	public CallResult(){
		super();
	}
	
	public CallResult(int code,String description,Object businessResult){
		super();
		this.code = code;
		this.description = description;
		this.businessResult = businessResult;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getBusinessResult() {
		return businessResult;
	}
	public void setBusinessResult(Object businessResult) {
		this.businessResult = businessResult;
	}
	
	
}

