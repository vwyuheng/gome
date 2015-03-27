package com.gome.model.enu;



public enum ResultEnum {
	
	SUCCESS	("success",	"成功"),
	ERROR	("error",	"失败"),
	ERROR_UNKONW	("error_unknow",	"未知错误");
	private String code;
	private String description;
	
	public static ResultEnum getResultStatusEnum(String code){
		
		if("success".equals(code)){
			return ResultEnum.SUCCESS;
		}
		if("error".equals(code)){
			return ResultEnum.ERROR;
		}
		
		return ResultEnum.ERROR_UNKONW;
	}
	
	private ResultEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
