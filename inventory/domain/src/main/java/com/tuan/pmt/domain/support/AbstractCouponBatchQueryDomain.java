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
 * <p>�����ϸ˵��</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: ������������Ϣ�������޹�˾</p> 
 * @author  ����־
 * @version 1.0 2012-11-26 ����־
 * <p>          �޸������� �޸�����˵��</p>
 * @see     �ο���1
 */
public class AbstractCouponBatchQueryDomain {
    
    /** ����ID */
    private Long batchId;
    /** ���ŷ�ʽ���Զ����˹�  enum('auto','manual')*/
    private String assignType;
    /** ǰ̨���� */
    private String name;
    /** ��̨���ƻ�˵�� */
    private String backendName;
    /** ����ʱ�� */
    private String genTime;
    /** ��ʼʱ�� */
    private String startTime;
    /** ����ʱ�� */
    private String endTime;
    /** ��� */
    private Integer faceValue;
    /** ���� */
    private Long num;
    /** ������ID */
    private Long adminId;
    /** ���벿�� */
    private String department;
    /** ��ע */
    private String comment;
    /** ����ʱ�� */
    private String invalidTime;
    /** ���ϲ����� */
    private Long invalidAdminId;
    /** �������� */
    private String invalidReason;
    /** ���÷�Χ���ֻ��ˡ���վ��  set('phone','web')*/
    private String useTerminal;
    /** �󶨴������� */
    private Integer bindLimit;
    /** ��Ʒ�������� */
    private BigDecimal goodsPrice;
    /** ��Ʒ���Ͻ������ */
    private BigDecimal goodsSetAmount;
    /** ��ʹ�õ�һ����ƷID */
    private String goodsIds;
    /** �����״ι���ɰ� */
    private Integer firstTime;
    /** �����Ѱ��ֻ��ŵ��û��ɰ󶨴˴���ȯ */
    private Integer bindPhone;
    /** ��ʹ������ */
    private Long usedNum;
    /** �Ѱ����� */
    private Long bindNum;
    /** �������� */
    private Long freezeNum;
    /** ���������� */
    private Long invalidNum;
    /** �˿���  decimal(10,2)*/
    private BigDecimal refundAmount;
    /** ��ʹ�ý��  decimal(10,2)*/
    private BigDecimal usedAmount;
    /** �û��������أ� */
    private Long userNum;
    /** ����ȯ�����İ�������ȯ֧�����ֵ����۶�  decimal(10,2)*/
    private BigDecimal goodsAmount;
    /** ״̬ ���ѱ���(0)��������(1)��������(2)��������(3) */
    private Integer status;
    /** �����Ƴ��� */
    private Integer allCity;
    /** �����Ʒ��� */
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
     * ��domainת��Ϊmodel
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
