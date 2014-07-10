package com.tuan.inventory.model.enu;

import com.tuan.inventory.model.util.QueueConstant;


/**
 * 公共类型信息枚举
 * 
 * @author
 * 
 */
public enum PublicCodeEnum {

	SYSTEM_EXCETION(QueueConstant.SERVICE_SYSTEM_FALIURE,"SERVICE_SYSTEM_FALIURE"),
	INVALID_GOODSID(QueueConstant.INVALID_GOODSID,"INVALID_GOODSID"),
	SYSTEM_DB_EXCEPTION(QueueConstant.SERVICE_DATABASE_FALIURE,"SERVICE_DATABASE_FAILURE"),
	SYSTEM_REDIS_EXCEPTION(QueueConstant.SERVICE_REDIS_FALIURE,"SERVICE_REDIS_FALIURE"),
	SUCCESS(QueueConstant.SUCCESS,"SUCCESS"),
	PARAM_INVALID(QueueConstant.INVALID_PARAM,"INVALID_PARAM"),
	NO_DATA(QueueConstant.NO_DATA,"NO_DATA"),
	NO_GOODS(QueueConstant.NO_GOODS,"NO_GOODS"),
	INVALID_SELECTIONID(QueueConstant.INVALID_SELECTIONID,"INVALID_SELECTIONID"),
	INVALID_SUPPLIERSID(QueueConstant.INVALID_SUPPLIERSID,"INVALID_SUPPLIERSID"),
	DATA_EXISTED(QueueConstant.DATA_EXISTED,"DATA_EXISTED"),
	UNKNOW_ERROR(QueueConstant.UNKNOW_ERROR,"UNKNOW_ERROR"),
	NO_GOODSBASE(QueueConstant.NO_GOODSBASE,"NO_GOODSBASE"),
	//addd
	FAIL_ADJUST_INVENTORY(QueueConstant.FAIL_ADJUST_INVENTORY,"FAIL_ADJUST_INVENTORY"),
	
	INVALID_IP(QueueConstant.INVALID_IP,"INVALID_IP"),
	INVALID_CLIENT(QueueConstant.INVALID_CLIENT,"INVALID_CLIENT"),
	INVALID_TIME(QueueConstant.INVALID_TIME,"INVALID_TIME"),
	INVALID_RETURN(QueueConstant.INVALID_RETURN,"INVALID_RETURN");
	
	private int code;
	private String message;

	private PublicCodeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static PublicCodeEnum valuesOf(int code) {
		if (code == QueueConstant.SERVICE_SYSTEM_FALIURE) {
			return SYSTEM_EXCETION;
		}else if (code == QueueConstant.SUCCESS) {
			return SUCCESS;
		} else if (code == QueueConstant.INVALID_PARAM) {
			return PARAM_INVALID;
		} else if (code == QueueConstant.NO_DATA) {
			return NO_DATA;
		} else if (code == QueueConstant.DATA_EXISTED) {
			return DATA_EXISTED;
		} else if(code == QueueConstant.INVALID_GOODSID){
			return INVALID_GOODSID;
		} else if(code == QueueConstant.INVALID_SELECTIONID){
			return INVALID_SELECTIONID;
		} else if(code == QueueConstant.INVALID_SUPPLIERSID){
			return INVALID_SUPPLIERSID;
		} else if(code == QueueConstant.SERVICE_DATABASE_FALIURE){
			return SYSTEM_DB_EXCEPTION;
		} else if(code == QueueConstant.SERVICE_REDIS_FALIURE){
			return SYSTEM_REDIS_EXCEPTION;
		} else if(code == QueueConstant.FAIL_ADJUST_INVENTORY){
			return FAIL_ADJUST_INVENTORY;
		} 
		else if(code == QueueConstant.INVALID_IP){
			return INVALID_IP;
		} else if(code == QueueConstant.INVALID_CLIENT){
			return INVALID_CLIENT;
		} else if(code == QueueConstant.INVALID_TIME){
			return INVALID_TIME;
		} else if(code == QueueConstant.INVALID_RETURN){
			return INVALID_RETURN;
		} else if(code == QueueConstant.NO_GOODSBASE){
			return NO_GOODSBASE;
		}else if (code == QueueConstant.NO_GOODS) {
			return NO_GOODS;
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
