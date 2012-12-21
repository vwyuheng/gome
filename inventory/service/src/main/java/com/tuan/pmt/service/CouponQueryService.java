package com.tuan.pmt.service;

import java.util.List;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.core.common.page.PageList;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponQueryResult;

/**
 * 代金券查询接口定义
 * @author duandj
 *
 */
public interface CouponQueryService {

	/**
	 * 根据传入的用户ID和券状态列表，查询用户下的代金券。
	 * @param userId 用户ID（非空）
	 * @param statusList	值参照 com.tuan.pmt.model.constant.status.CouponStatusEnum 空： 返回用户下的所有代金券   
	 * @param platType   平台类型
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示条数
	 * @return CallResult<CouponQueryResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponQueryResult
	 * @see PageList   
	 */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(final long userId,final List<Integer> statusList,final String platType,final int currPage,final int pageSize);
	
	/**
	 * 根据传入的代金券ID，查询代金券及批次的详细信息。
	 * @param userId 用户ID（非空）
	 * @param statusList	空： 返回用户下的所有代金券
	 * @return CallResult<CouponQueryResult> 接口调用结果对象
	 * 
	 * @see	CallResult   businessObject 代金券批次信息
	 * @see	CouponQueryResult -- 代金券信息
	 * @see CpnCouponModel 
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(final long couponId);
	
	
	/**
     * 根据批次id查询批次信息
     * @param batchId	批次id
     * @return	CallResult<CouponQueryResult> 接口调用结果对象
     * 
     * @see CallResult   businessObject 代金券批次信息
     * @see CouponQueryResult -- 代金券信息
     * @see CpnCouponBatchModel -- 批次信息
     */
    @ProductCode(code = "00002", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponBatchModel>> queryCouponBatchByBatchId(final long batchId);
    
    /**
     * 根据传入的代金券验证码，查询代金券、及批次的详细信息。
     * @param userId 用户ID（非空）
     * @param statusList    空： 返回用户下的所有代金券
     * @return CallResult<CouponQueryResult> 接口调用结果对象
     * 
     * @see CallResult   businessObject 代金券批次信息
     * @see CouponQueryResult -- 代金券信息
     * @see CpnCouponModel -- 代金券模型对象
     */
    @ProductCode(code = "00003", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(final String couponCode);
    
    /**
     * 根据订单ID，用户ID及状态列表，查询代金券、及批次的详细信息。
     * @param userId 	用户ID（非空）
     * @param orderId	订单ID（非空）   
     * @param statusList    空： 不限制状态
     * @return CallResult<CouponQueryResult> 接口调用结果对象
     * 
     * @see CallResult   businessObject 代金券批次信息
     * @see CouponQueryResult -- 代金券信息
     * @see CpnCouponModel -- 代金券模型对象
     */
    @ProductCode(code = "00004", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(final long userId,final long orderId,final List<Integer> statusList);
}
