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
 * <p>平台来源描述</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: 北京窝窝团信息技术有限公司</p> 
 * @author  陈立志
 * @version 1.0 2012-11-26 陈立志
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public enum CouponPlatTypeEnum {
    PLATTYPE_PHONE  ( 1,    "phone",          "手机平台"),
    PLATTYPE_WEB    ( 2,    "web",            "web平台"),
    UNKNOW          (-101,  "UNKNOW",         "未知");
    
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
