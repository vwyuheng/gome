package com.tuan.pmt.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponUpdateResult;

/**
 * 代金券服务接口定义
 * @author duandj
 *
 */
public interface CouponUpdateService {
	
	/**
	 * 代金券绑定接口，用户与代金券的绑定。
	 * @param userId		用户ID（非空）
	 * @param couponCode	代金券验证码（非空）
	 * @param platType      'phone','web'（非空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00010", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> bindCoupon2User(final long userId,final String couponCode,final String platType);
	
	/**
	 * 代金券冻结接口,订单与代金券的绑定
	 * @param userId 	用户ID	（非空）
	 * @param couponId 	代金券ID	（非空）
	 * @param orderId 	订单ID	（非空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00011", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponFreeze(final long userId,final long couponId,final long orderId);
	
	/**
	 * 代金券解冻接口
	 * @param userId 	用户ID	（非空）
	 * @param couponId 	代金券ID	（非空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00012", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreeze(final long userId,final long couponId);
	
	/**
	 * 代金券解冻接口,将订单下冻结的券解冻
	 * @param userId 	用户ID	（非空）
	 * @param orderId 	订单ID	（非空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00013", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreezeByOrderId(final long userId,final long orderId);
	
	/**
	 * 代金券使用接口
	 * @param userId 	用户ID	（非空）
	 * @param couponId 	代金券ID	（非空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00014", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUsed(final long userId,final long couponId);
	
	/**
	 * 单个代金券作废接口
	 * @param userId 	用户ID	（非空）
	 * @param couponId 	代金券ID	（非空）
	 * @param invalidAdminId	作废操作人ID	（不建议空）
	 * @param invalidReason		作废理由		（不建议空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00015", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancel(final long userId,final long couponId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * 代金券作废接口,根据订单ID
	 * @param userId 	用户ID	（非空）
	 * @param orderId 	订单ID	（非空）
	 * @param invalidAdminId	作废操作人ID	（不建议空）
	 * @param invalidReason		作废理由		（不建议空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00016", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancelByOrderId(final long userId,final long orderId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * 代金券退款接口,根据订单ID
	 * @param userId 	用户ID	（非空）
	 * @param orderId 	订单ID	（非空）
	 * @param invalidAdminId	作废操作人ID	（不建议空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00017", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponRefundByOrderId(final long userId,final long orderId,
			final long invalidAdminId);
	
	/**
	 * 代金券批次作废接口
	 * @param userId 	用户ID	（非空）
	 * @param couponId 	代金券ID	（非空）
	 * @param invalidAdminId	作废操作人ID	（不建议空）
	 * @param invalidReason		作废理由		（不建议空）
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00018", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponBatchCancel(
			final long batchId,final long invalidAdminId,final String invalidReason);
}