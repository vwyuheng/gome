package com.tuan.pmt.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;

/**
 * ����ȯУ��ӿڶ���
 * @author duandj
 *
 */
public interface CouponCheckService {
	
	/**
	 * ����ȯУ��ӿڣ�У���û��Ĵ���ȯ�Ƿ�������ڸ���Ʒ�ϡ�
	 * @param userId	�û�ID	���ǿգ�
	 * @param couponId	����ȯID	���ǿգ�
	 * @param orderFrom	������Դ:'phone','web' ���ǿգ�
	 * @param goodsInfoParam	��Ʒ����	���ǿգ�
	 * @return CallResult<CouponCheckResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponCheckResult
	 */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponCheckResult> checkCoupon(final long userId,final long couponId,
			final String orderFrom,final GoodsInfoParam goodsInfoParam);
}
