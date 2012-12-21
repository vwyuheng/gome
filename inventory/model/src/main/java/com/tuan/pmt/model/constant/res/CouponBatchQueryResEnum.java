package com.tuan.pmt.model.constant.res;

/**
 * <p>CouponBatchQueryResEnum</p>
 * <p>��ѯ����ȯ������Ϣ���ؽ������</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: ������������Ϣ�������޹�˾</p> 
 * @author  ����־
 * @version 1.0 2012-11-26 ����־
 * <p>          �޸������� �޸�����˵��</p>
 * @see     �ο���1
 */
public enum CouponBatchQueryResEnum {
    SUCCESS         ( 1,    "SUCCESS",          "�ɹ�"),
    INVALID_BATCHID (-1,    "INVALID_BATCHID",  "����id��Ч"),
    BATCHID_NOTIEXIST(-2,   "BATCHID_NOTIEXIST","�����β�����"),
    DB_ERROR        (-99,   "DB_ERROR",         "���ݿ����"),
    SYS_ERROR       (-100,  "SYS_ERROR",        "ϵͳ����"),
    UNKNOW          (-101,  "UNKNOW",           "δ֪");
    
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
     * ����code ֱ�ӻ�ȡ������Ϣ
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
