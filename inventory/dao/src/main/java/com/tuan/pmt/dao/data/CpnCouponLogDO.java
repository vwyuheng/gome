package com.tuan.pmt.dao.data;

import java.sql.Timestamp;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券日志表（cpn_coupon_log）
 * @ClassName: CpnCouponLog
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponLogDO extends TuanBaseDO{
	/** 序列化标识 */
	private static final long serialVersionUID = 5651317059372625677L;

	/** id */
	private Long logId;
	/** 代金券id */
	private Long couponId;
	/** 创建时间 */
	private Timestamp createTime;
	/** 类型码 用来定义代金券有哪种类型的操作和变化。目前未明确定义值。大概有“创建”,"绑定"，“冻结”，“使用”，“作废”，“退款” */
	private String code;
	/** 描述 */
	private String description;
	/** 对象ID，user_id or order_id */
	private Long objectId;
	
	public Long getLogId() {
		return logId;
	}
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
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
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
