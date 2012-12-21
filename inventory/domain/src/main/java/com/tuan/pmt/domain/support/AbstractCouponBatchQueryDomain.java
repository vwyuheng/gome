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

package com.tuan.pmt.domain.support;

import java.math.BigDecimal;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.domain.support.util.DateUtils;
import com.tuan.pmt.model.CpnCouponBatchModel;

/**
 * <p>AbstractCouponBatchQueryDomain</p>
 * <p>类的详细说明</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: 北京窝窝团信息技术有限公司</p> 
 * @author  陈立志
 * @version 1.0 2012-11-26 陈立志
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class AbstractCouponBatchQueryDomain {
    
    /** 批次ID */
    private Long batchId;
    /** 发放方式：自动、人工  enum('auto','manual')*/
    private String assignType;
    /** 前台名称 */
    private String name;
    /** 后台名称或说明 */
    private String backendName;
    /** 生成时间 */
    private String genTime;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 面额 */
    private Integer faceValue;
    /** 数量 */
    private Long num;
    /** 创建人ID */
    private Long adminId;
    /** 申请部门 */
    private String department;
    /** 备注 */
    private String comment;
    /** 作废时间 */
    private String invalidTime;
    /** 作废操作人 */
    private Long invalidAdminId;
    /** 作废理由 */
    private String invalidReason;
    /** 适用范围：手机端、网站端  set('phone','web')*/
    private String useTerminal;
    /** 绑定次数限制 */
    private Integer bindLimit;
    /** 商品单价限制 */
    private BigDecimal goodsPrice;
    /** 商品集合金额限制 */
    private BigDecimal goodsSetAmount;
    /** 可使用的一组商品ID */
    private String goodsIds;
    /** 限制首次购买可绑定 */
    private Integer firstTime;
    /** 限制已绑定手机号的用户可绑定此代金券 */
    private Integer bindPhone;
    /** 已使用数量 */
    private Long usedNum;
    /** 已绑定数量 */
    private Long bindNum;
    /** 冻结数量 */
    private Long freezeNum;
    /** 已作废数量 */
    private Long invalidNum;
    /** 退款金额  decimal(10,2)*/
    private BigDecimal refundAmount;
    /** 已使用金额  decimal(10,2)*/
    private BigDecimal usedAmount;
    /** 用户数（排重） */
    private Long userNum;
    /** 代金券产生的包含代金券支付部分的销售额  decimal(10,2)*/
    private BigDecimal goodsAmount;
    /** 状态 ：已保存(0)、生成中(1)、已生成(2)、已作废(3) */
    private Integer status;
    /** 不限制城市 */
    private Integer allCity;
    /** 不限制分类 */
    private Integer allCat;
    
    /**
     * @param cpnCouponBatchDO
     */
    public void fillDomainByDo(CpnCouponBatchDO cpnCouponBatchDO) {
        if(null != cpnCouponBatchDO){
        
        this.batchId             = cpnCouponBatchDO.getBatchId        ();
        this.assignType          = cpnCouponBatchDO.getAssignType     ();   
        this.name                = cpnCouponBatchDO.getName           ();
        this.backendName         = cpnCouponBatchDO.getBackendName    ();  
        
        if(null != cpnCouponBatchDO.getGenTime()){
            this.genTime          = DateUtils.timestampToString(cpnCouponBatchDO.getGenTime    (),null);
        }
        if(null != cpnCouponBatchDO.getStartTime      ()){
            this.startTime          = DateUtils.timestampToString(cpnCouponBatchDO.getStartTime    (),null);
        }
       
        if(null != cpnCouponBatchDO.getEndTime        ()){
            this.endTime             = DateUtils.timestampToString(cpnCouponBatchDO.getEndTime    (),null);
        }
        
        this.faceValue           = cpnCouponBatchDO.getFaceValue      ();  
        this.num                 = cpnCouponBatchDO.getNum            ();
        this.adminId             = cpnCouponBatchDO.getAdminId        ();
        this.department          = cpnCouponBatchDO.getDepartment     ();   
        this.comment             = cpnCouponBatchDO.getComment        ();
        
        if(null != cpnCouponBatchDO.getInvalidTime        ()){
            this.invalidTime             = DateUtils.timestampToString(cpnCouponBatchDO.getInvalidTime    (),null);
        }
        this.invalidAdminId      = cpnCouponBatchDO.getInvalidAdminId ();       
        this.invalidReason       = cpnCouponBatchDO.getInvalidReason  ();      
        this.useTerminal         = cpnCouponBatchDO.getUseTerminal    ();    
        this.bindLimit           = cpnCouponBatchDO.getBindLimit      ();  
        this.goodsPrice          = cpnCouponBatchDO.getGoodsPrice     ();   
        this.goodsSetAmount      = cpnCouponBatchDO.getGoodsSetAmount ();       
        this.goodsIds            = cpnCouponBatchDO.getGoodsIds       (); 
        this.firstTime           = cpnCouponBatchDO.getFirstTime      ();  
        this.bindPhone           = cpnCouponBatchDO.getBindPhone      ();  
        this.usedNum             = cpnCouponBatchDO.getUsedNum        ();
        this.bindNum             = cpnCouponBatchDO.getBindNum        ();
        this.freezeNum           = cpnCouponBatchDO.getFreezeNum      ();  
        this.invalidNum          = cpnCouponBatchDO.getInvalidNum     ();   
        this.refundAmount        = cpnCouponBatchDO.getRefundAmount   ();     
        this.usedAmount          = cpnCouponBatchDO.getUsedAmount     ();   
        this.userNum             = cpnCouponBatchDO.getUserNum        ();
        this.goodsAmount         = cpnCouponBatchDO.getGoodsAmount    ();    
        this.status              = cpnCouponBatchDO.getStatus         ();
        this.allCity             = cpnCouponBatchDO.getAllCity        ();
        this.allCat              = cpnCouponBatchDO.getAllCat         ();
        }
    }
    
    /**
     * 将domain转化为model
     * @return
     */
    public CpnCouponBatchModel toModel() {
        CpnCouponBatchModel model = new CpnCouponBatchModel();
        model.setBatchId       (this.getBatchId        ());           
        model.setAssignType    (this.getAssignType     ());           
        model.setName          (this.getName           ());           
        model.setBackendName   (this.getBackendName    ());           
        model.setGenTime       (this.getGenTime        ());           
        model.setStartTime     (this.getStartTime      ());           
        model.setEndTime       (this.getEndTime        ());           
        model.setFaceValue     (this.getFaceValue      ());           
        model.setNum           (this.getNum            ());           
        model.setAdminId       (this.getAdminId        ());           
        model.setDepartment    (this.getDepartment     ());           
        model.setComment       (this.getComment        ());           
        model.setInvalidTime   (this.getInvalidTime    ());           
        model.setInvalidAdminId(this.getInvalidAdminId ());           
        model.setInvalidReason (this.getInvalidReason  ());           
        model.setUseTerminal   (this.getUseTerminal    ());           
        model.setBindLimit     (this.getBindLimit      ());           
        model.setGoodsPrice    (this.getGoodsPrice     ());           
        model.setGoodsSetAmount(this.getGoodsSetAmount ());           
        model.setGoodsIds      (this.getGoodsIds       ());           
        model.setFirstTime     (this.getFirstTime      ());           
        model.setBindPhone     (this.getBindPhone      ());           
        model.setUsedNum       (this.getUsedNum        ());           
        model.setBindNum       (this.getBindNum        ());           
        model.setFreezeNum     (this.getFreezeNum      ());           
        model.setInvalidNum    (this.getInvalidNum     ());           
        model.setRefundAmount  (this.getRefundAmount   ());           
        model.setUsedAmount    (this.getUsedAmount     ());           
        model.setUserNum       (this.getUserNum        ());           
        model.setGoodsAmount   (this.getGoodsAmount    ());           
        model.setStatus        (this.getStatus         ());           
        model.setAllCity       (this.getAllCity        ());           
        model.setAllCat        (this.getAllCat         ());           
        return model;
    }
    
    /**
     * @param batchModel
     */
    public void fillDomainByDomain(CpnCouponBatchModel batchModel) {
        if(null != batchModel){
            
            this.batchId             = batchModel.getBatchId        ();
            this.assignType          = batchModel.getAssignType     ();   
            this.name                = batchModel.getName           ();
            this.backendName         = batchModel.getBackendName    ();    
            this.genTime             = batchModel.getGenTime        ();
            this.startTime           = batchModel.getStartTime      ();  
            this.endTime             = batchModel.getEndTime        ();
            this.faceValue           = batchModel.getFaceValue      ();  
            this.num                 = batchModel.getNum            ();
            this.adminId             = batchModel.getAdminId        ();
            this.department          = batchModel.getDepartment     ();   
            this.comment             = batchModel.getComment        ();
            this.invalidTime         = batchModel.getInvalidTime    ();    
            this.invalidAdminId      = batchModel.getInvalidAdminId ();       
            this.invalidReason       = batchModel.getInvalidReason  ();      
            this.useTerminal         = batchModel.getUseTerminal    ();    
            this.bindLimit           = batchModel.getBindLimit      ();  
            this.goodsPrice          = batchModel.getGoodsPrice     ();   
            this.goodsSetAmount      = batchModel.getGoodsSetAmount ();       
            this.goodsIds            = batchModel.getGoodsIds       (); 
            this.firstTime           = batchModel.getFirstTime      ();  
            this.bindPhone           = batchModel.getBindPhone      ();  
            this.usedNum             = batchModel.getUsedNum        ();
            this.bindNum             = batchModel.getBindNum        ();
            this.freezeNum           = batchModel.getFreezeNum      ();  
            this.invalidNum          = batchModel.getInvalidNum     ();   
            this.refundAmount        = batchModel.getRefundAmount   ();     
            this.usedAmount          = batchModel.getUsedAmount     ();   
            this.userNum             = batchModel.getUserNum        ();
            this.goodsAmount         = batchModel.getGoodsAmount    ();    
            this.status              = batchModel.getStatus         ();
            this.allCity             = batchModel.getAllCity        ();
            this.allCat              = batchModel.getAllCat         ();
            }
        
    }
  
    
    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
    public String getAssignType() {
        return assignType;
    }
    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBackendName() {
        return backendName;
    }
    public void setBackendName(String backendName) {
        this.backendName = backendName;
    }
    public String getGenTime() {
        return genTime;
    }
    public void setGenTime(String genTime) {
        this.genTime = genTime;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public Integer getFaceValue() {
        return faceValue;
    }
    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }
    public Long getNum() {
        return num;
    }
    public void setNum(Long num) {
        this.num = num;
    }
    public Long getAdminId() {
        return adminId;
    }
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getInvalidTime() {
        return invalidTime;
    }
    public void setInvalidTime(String invalidTime) {
        this.invalidTime = invalidTime;
    }
    public Long getInvalidAdminId() {
        return invalidAdminId;
    }
    public void setInvalidAdminId(Long invalidAdminId) {
        this.invalidAdminId = invalidAdminId;
    }
    public String getInvalidReason() {
        return invalidReason;
    }
    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }
    public String getUseTerminal() {
        return useTerminal;
    }
    public void setUseTerminal(String useTerminal) {
        this.useTerminal = useTerminal;
    }
    public Integer getBindLimit() {
        return bindLimit;
    }
    public void setBindLimit(Integer bindLimit) {
        this.bindLimit = bindLimit;
    }
    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }
    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public BigDecimal getGoodsSetAmount() {
        return goodsSetAmount;
    }
    public void setGoodsSetAmount(BigDecimal goodsSetAmount) {
        this.goodsSetAmount = goodsSetAmount;
    }
    public String getGoodsIds() {
        return goodsIds;
    }
    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }
    public Integer getFirstTime() {
        return firstTime;
    }
    public void setFirstTime(Integer firstTime) {
        this.firstTime = firstTime;
    }
    public Integer getBindPhone() {
        return bindPhone;
    }
    public void setBindPhone(Integer bindPhone) {
        this.bindPhone = bindPhone;
    }
    public Long getUsedNum() {
        return usedNum;
    }
    public void setUsedNum(Long usedNum) {
        this.usedNum = usedNum;
    }
    public Long getBindNum() {
        return bindNum;
    }
    public void setBindNum(Long bindNum) {
        this.bindNum = bindNum;
    }
    public Long getFreezeNum() {
        return freezeNum;
    }
    public void setFreezeNum(Long freezeNum) {
        this.freezeNum = freezeNum;
    }
    public Long getInvalidNum() {
        return invalidNum;
    }
    public void setInvalidNum(Long invalidNum) {
        this.invalidNum = invalidNum;
    }
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    public BigDecimal getUsedAmount() {
        return usedAmount;
    }
    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }
    public Long getUserNum() {
        return userNum;
    }
    public void setUserNum(Long userNum) {
        this.userNum = userNum;
    }
    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }
    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getAllCity() {
        return allCity;
    }
    public void setAllCity(Integer allCity) {
        this.allCity = allCity;
    }
    public Integer getAllCat() {
        return allCat;
    }
    public void setAllCat(Integer allCat) {
        this.allCat = allCat;
    }

}
