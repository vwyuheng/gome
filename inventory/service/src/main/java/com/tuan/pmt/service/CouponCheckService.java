package com.tuan.pmt.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;

/**
 * 代金券校验接口定义
 * @author duandj
 *
 */
public interface CouponCheckService {
	
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
}
