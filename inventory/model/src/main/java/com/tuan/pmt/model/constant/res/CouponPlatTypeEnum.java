/*
 *    Copyright (c) 2001-2010 WoWoTuan Ltd.
 *    All rights reserved
 *
 *    This is unpublished proprietary source code of WoWoTuan Ltd.
 *    The copyright notice above does not evidence any actual
 *    or intended publication of such source code.
 *
 *    NOTICE: UNAUTHORIZED DISTRIBUTION, ADAPTATION OR USE MAY BE
 *    SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 */

package com.tuan.pmt.model.constant.res;

/**
 * <p>CouponPlatTypeEnum</p>
 * <p>ƽ̨��Դ����</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: ������������Ϣ�������޹�˾</p> 
 * @author  ����־
 * @version 1.0 2012-11-26 ����־
 * <p>          �޸������� �޸�����˵��</p>
 * @see     �ο���1
 */
public enum CouponPlatTypeEnum {
    PLATTYPE_PHONE  ( 1,    "phone",          "�ֻ�ƽ̨"),
    PLATTYPE_WEB    ( 2,    "web",            "webƽ̨"),
    UNKNOW          (-101,  "UNKNOW",         "δ֪");
    
    private int code;
    private String name;
    private String description;
    
    private CouponPlatTypeEnum(int code,String name,String description){
        this.code = code ;
        this.name = name;
        this.description = description;
    }
    
    public static CouponPlatTypeEnum valueOfEnum(int code) {
        switch (code) {
        case 1:
            return PLATTYPE_PHONE;
        case 2:
            return PLATTYPE_WEB;
        default:
            return UNKNOW;
        }
    }
    
    
    public static CouponPlatTypeEnum valueOfEnum(String name) {
        if(PLATTYPE_PHONE.getName().equalsIgnoreCase(name)){
            return PLATTYPE_PHONE;
        }else if(PLATTYPE_WEB.getName().equalsIgnoreCase(name)){
            return PLATTYPE_WEB;
        }
        return UNKNOW;
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
