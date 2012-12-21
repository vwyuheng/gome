package com.tuan.pmt.client;
import java.util.List;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.core.common.page.PageList;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.model.result.CouponUpdateResult;


public interface YouhuiCenterFacade {
	/**
	 * 代金券校验接口，校验用户的代金券是否可以用在该商品上。
	 * @param userId	用户ID	（非空）
	 * @param couponId	代金券ID	（非空）
	 * @param orderFrom	订单来源:'phone','web' （非空）
	 * @param goodsInfoParam	商品属性	（非空）
	 * @return CallResult<CouponCheckResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponCheckResult
	 */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponCheckResult> checkCoupon(final long userId,final long couponId,
			final String orderFrom,final GoodsInfoParam goodsInfoParam);
	
	/**
     * 根据传入的用户ID和券状态列表，查询用户下的代金券。
     * @param userId 用户ID（非空）
     * @param statusList    值参照 com.tuan.pmt.model.constant.status.CouponStatusEnum 空： 返回用户下的所有代金券   
     * @param platType   平台类型
     * @param currPage 当前第几页
     * @param pageSize 每页显示条数
     * @return CallResult<CouponQueryResult> 接口调用结果对象
     * 
     * @see CallResult
     * @see CouponQueryResult
     * @see CpnCouponModel   
     */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(final long userId,final List<Integer> statusList,final String platType ,final int currPage,final int pageSize);
	
	/**
	 * 根据传入的代金券ID，查询代金券及批次的详细信息。
	 * @param userId 用户ID（非空）
	 * @param statusList	空： 返回用户下的所有代金券
	 * @return CallResult<CouponQueryResult> 接口调用结果对象
	 * 
	 * @see	CallResult   businessObject 代金券批次信息
	 * @see	CouponQueryResult -- 代金券信息
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(final long couponId);
	
	
	/**
     * 根据批次id查询批次信息
     * @param batchId
     * @return
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
     */
    @ProductCode(code = "00003", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(final String couponCode);
	
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
	 * 代金券冻结接口
	 * @param userId 	用户ID
	 * @param couponId 	代金券ID
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00011", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponFreeze(final long userId,final long couponId,final long orderId);
	
	/**
	 * 代金券解冻接口
	 * @param userId 	用户ID
	 * @param couponId 	代金券ID
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00012", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreeze(final long userId,final long couponId);
	
	/**
	 * 代金券使用接口
	 * @param userId 	用户ID
	 * @param couponId 	代金券ID
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00013", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUsed(final long userId,final long couponId);
	
	/**
	 * 单个代金券作废接口
	 * @param userId 	用户ID
	 * @param couponId 	代金券ID
	 * @param invalidAdminId	作废操作人ID
	 * @param invalidReason		作废理由
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00014", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancel(final long userId,final long couponId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * 代金券作废接口,根据订单ID
	 * @param userId 	用户ID
	 * @param orderId 	订单ID
	 * @param invalidAdminId	作废操作人ID
	 * @param invalidReason		作废理由
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00016", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancelByOrderId(final long userId,final long orderId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * 代金券批次作废接口
	 * @param userId 	用户ID
	 * @param couponId 	代金券ID
	 * @param invalidAdminId	作废操作人ID
	 * @param invalidReason		作废理由
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00015", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponBatchCancel(
			final long batchId,final long invalidAdminId,final String invalidReason);
	
	/**
	 * 代金券解冻接口,将订单下冻结的券解冻
	 * @param userId 	用户ID
	 * @param orderId 	订单ID
	 * @return CallResult<CouponUpdateResult> 接口调用结果对象
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	CallResult<CouponUpdateResult> couponUnFreezeByOrderId(final long userId,final long orderId);
	
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
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(final long userId,final long orderId,final List<Integer> statusList);

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
	CallResult<CouponUpdateResult> couponRefundByOrderId(final long userId,final long orderId,final long invalidAdminId);
}
