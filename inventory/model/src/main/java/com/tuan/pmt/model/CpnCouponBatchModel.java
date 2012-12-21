package com.tuan.pmt.model;

import java.math.BigDecimal;
import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * <p>CpnCouponBatchDO</p>
 * <p>批次信息</p>
 * <p>Copyright: Copyright (c) 2010</p> 
 * <p>Company: 北京窝窝团信息技术有限公司</p> 
 * @author  陈立志
 * @version 1.0 2012-11-26 陈立志
 * <p>          修改者姓名 修改内容说明</p>
 * @see     参考类1
 */
public class CpnCouponBatchModel extends TuanBaseDO{
    private static final long serialVersionUID = -4007767679898719668L;
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
    
	/**限制城市Id列表*/
	private List<Long> cityIdList;
	/**限制分类Id列表 ，二级分类ID*/
	private List<Long> catIdList;
    
    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public String getAssignType() {
        return assignType;
    }
    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }
    public String getBackendName() {
        return backendName;
    }
    public void setBackendName(String backendName) {
        this.backendName = backendName;
    }
    public Integer getFaceValue() {
        return faceValue;
    }
    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }
    public String getInvalidReason() {
        return invalidReason;
    }
    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }
    public List<Long> getCityIdList() {
		return cityIdList;
	}
	public void setCityIdList(List<Long> cityIdList) {
		this.cityIdList = cityIdList;
	}
	public List<Long> getCatIdList() {
		return catIdList;
	}
	public void setCatIdList(List<Long> catIdList) {
		this.catIdList = catIdList;
	}
}
