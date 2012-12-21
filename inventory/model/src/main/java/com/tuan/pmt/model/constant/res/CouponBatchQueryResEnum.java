package com.tuan.pmt.model.constant.res;

/**
 * <p>CouponBatchQueryResEnum</p>
 * <p>查询代金券批次信息返回结果描述</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: 北京窝窝团信息技术有限公司</p> 
 * @author  陈立志
 * @version 1.0 2012-11-26 陈立志
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public enum CouponBatchQueryResEnum {
    SUCCESS         ( 1,    "SUCCESS",          "成功"),
    INVALID_BATCHID (-1,    "INVALID_BATCHID",  "批次id无效"),
    BATCHID_NOTIEXIST(-2,   "BATCHID_NOTIEXIST","该批次不存在"),
    DB_ERROR        (-99,   "DB_ERROR",         "数据库错误"),
    SYS_ERROR       (-100,  "SYS_ERROR",        "系统错误"),
    UNKNOW          (-101,  "UNKNOW",           "未知");
    
    private int code;
    private String name;
    private String description;
    
    private CouponBatchQueryResEnum(int code,String name,String description){
        this.code = code ;
        this.name = name;
        this.description = description;
    }
    
    public static CouponBatchQueryResEnum valueOfEnum(int code) {
        switch (code) {
        case 1:
            return SUCCESS;
        case -1:
            return INVALID_BATCHID;
        case -2:
            return BATCHID_NOTIEXIST;
        case -99:
            return DB_ERROR;
        case -100:
            return SYS_ERROR;
        default:
            return UNKNOW;
        }
    }
    
    /**
     * 根据code 直接获取描述信息
     * @param code
     * @return
     */
    public static String value2Description(int code){
        CouponBatchQueryResEnum enum1 = valueOfEnum(code);
        return enum1.getDescription();
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
