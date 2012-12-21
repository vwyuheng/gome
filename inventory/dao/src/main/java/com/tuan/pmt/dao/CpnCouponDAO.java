package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.core.common.page.PageList;
import com.tuan.pmt.dao.data.CpnCouponDO;


public interface CpnCouponDAO {
	/**
	 * 修改代金券表，根据代金券id
	 * @param cpnCouponDO 
	 * @return Integer 修改成功返回修改条数，否则返回0
	 */
	public Integer update(CpnCouponDO cpnCouponDO);
	
	/**
	 * 根据订单ID，查询代金券列表
	 * @param orderId
	 * @return	代金券对象列表 ，失败或无记录返回null
	 */
	public List<CpnCouponDO> queryCpnCouponByOrderId(final long orderId) ;
	
	/**
	 * 根据订单ID，查询代金券列表
	 * @param orderId	订单ID
	 * @param userId	用户ID
	 * @param statusList	状态ID ，null:不限制状态
	 * @return	代金券对象列表 ，失败或无记录返回null
	 */
	public List<CpnCouponDO> queryCouponDOByOrderIdAndStatus(final long orderId,final long userId,final List<Integer> statusList);
	
	/**
	 * 根据代金券ID，查询代金券
	 * @param couponId
	 * @return	代金券对象，失败或无记录返回null
	 */
	public CpnCouponDO queryCpnCouponByCouponId(final long couponId) ;
	
	/**
	 * 根据传入的用户ID和券状态列表，查询用户下的代金券。
	 * @param userId		用户ID
	 * @param statusList	要查询的状态列表
	 * @param pageSize 
	 * @param currPage 
	 * @param platType 
	 * @return
	 */
	public PageList queryCpnCouponByUserId(final long userId,final List<Integer> statusList, String platType, int currPage, int pageSize);
	
	/**
	 * 根据验证码，查询代金券
	 * @param code	验证码
	 * @return	代金券对象
	 */
	public CpnCouponDO queryCpnCouponByCode(String code);

    /**
     * 根据用户id,批次id查询代金券列表
     * @param userId
     * @param batchId
     * @return
     */
    public List<CpnCouponDO> queryCouponsByUserIdAndBatchId(long userId,
            long batchId);
}
