package com.tuan.inventory.model.enu;

import com.tuan.inventory.model.util.QueueConstant;


/**
 * 公共类型信息枚举
 * 
 * @author shaolong zhang
 * 
 */
public enum PublicCodeEnum {

	SYSTEM_EXCETION(QueueConstant.SERVICE_SYSTEM_FALIURE,"SERVICE_SYSTEM_FALIURE"),
	DB_EXCEPTION(QueueConstant.NO_ALIVE_DATASOURCE,"NO_ALIVE_DATASOURCE"),
	SYSTEM_DB_EXCEPTION(QueueConstant.SERVICE_DATABASE_FALIURE,"SERVICE_DATABASE_FAILURE"),
	SUCCESS(QueueConstant.SUCCESS,"SUCCESS"),
	PARAM_INVALID(QueueConstant.PARAMS_INVALID,"PARAMS_INVALID"),
	NO_DATA(QueueConstant.NO_DATA,"NO_DATA"),
	NOT_SAFE_IPADDRESS(QueueConstant.NOT_SAFE_IPADDRESS,"NOT_SAFE_IPADDRESS"),
	OVER_TOP_VALUE(QueueConstant.OVER_TOP_VALUE,"OVER_TOP_VALUE"),
	DATA_EXISTED(QueueConstant.DATA_EXISTED,"DATA_EXISTED"),
	NOT_SUPPORT(QueueConstant.NOT_SUPPORT,"NOT_SUPPORT"),
	UNKNOW_ERROR(QueueConstant.UNKNOW_ERROR,"UNKNOW_ERROR");
	
	private int code;
	private String message;

	private PublicCodeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static PublicCodeEnum valuesOf(int code) {
		if (code == QueueConstant.SERVICE_SYSTEM_FALIURE) {
			return SYSTEM_EXCETION;
		} else if (code == QueueConstant.NO_ALIVE_DATASOURCE) {
			return DB_EXCEPTION;
		} else if (code == QueueConstant.SUCCESS) {
			return SUCCESS;
		} else if (code == QueueConstant.PARAMS_INVALID) {
			return PARAM_INVALID;
		} else if (code == QueueConstant.NO_DATA) {
			return NO_DATA;
		} else if (code == QueueConstant.NOT_SAFE_IPADDRESS) {
			return NOT_SAFE_IPADDRESS;
		} else if(code == QueueConstant.DATA_EXISTED){
			return DATA_EXISTED;
		} else if(code == QueueConstant.SERVICE_DATABASE_FALIURE){
			return SYSTEM_DB_EXCEPTION;
		} else if(code == QueueConstant.NOT_SUPPORT){
			return NOT_SUPPORT;
		} else if(code == QueueConstant.OVER_TOP_VALUE){
			return OVER_TOP_VALUE;
		}
		return UNKNOW_ERROR;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public static void main(String[] args) {
		System.out.println(PublicCodeEnum.DATA_EXISTED.getCode());
	}
}
