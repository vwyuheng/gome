package com.tuan.pmt.dao.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * ����ȯ���α�cpn_coupon_batch��
 * @ClassName: CpnCouponBatch
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponBatchDO extends TuanBaseDO{
	/** ���л���ʶ */
	private static final long serialVersionUID = 7501357978779255515L;
	
	/** ����ID */
	private Long batchId;
	/** ���ŷ�ʽ���Զ����˹�  enum('auto','manual')*/
	private String assignType;
	/** ǰ̨���� */
	private String name;
	/** ��̨���ƻ�˵�� */
	private String backendName;
	/** ����ʱ�� */
	private Timestamp genTime;
	/** ��ʼʱ�� */
	private Timestamp startTime;
	/** ����ʱ�� */
	private Timestamp endTime;
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
	private Timestamp invalidTime;
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
	public Timestamp getGenTime() {
		return genTime;
	}
	public void setGenTime(Timestamp genTime) {
		this.genTime = genTime;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
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
	public Timestamp getInvalidTime() {
		return invalidTime;
	}
	public void setInvalidTime(Timestamp invalidTime) {
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
}
