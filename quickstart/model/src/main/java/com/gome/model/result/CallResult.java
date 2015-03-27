package com.gome.model.result;

import com.gome.core.common.lang.GomeBaseDO;

/** 
 * @author henry.yu
 * @param <T>
 *  业务返回对象
 * @param <T>
 */
public class CallResult extends GomeBaseDO{
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
