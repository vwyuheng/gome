package com.tuan.pmt.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponUpdateResult;

/**
 * ����ȯ����ӿڶ���
 * @author duandj
 *
 */
public interface CouponUpdateService {
	
	/**
	 * ����ȯ�󶨽ӿڣ��û������ȯ�İ󶨡�
	 * @param userId		�û�ID���ǿգ�
	 * @param couponCode	����ȯ��֤�루�ǿգ�
	 * @param platType      'phone','web'���ǿգ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00010", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> bindCoupon2User(final long userId,final String couponCode,final String platType);
	
	/**
	 * ����ȯ����ӿ�,���������ȯ�İ�
	 * @param userId 	�û�ID	���ǿգ�
	 * @param couponId 	����ȯID	���ǿգ�
	 * @param orderId 	����ID	���ǿգ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00011", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponFreeze(final long userId,final long couponId,final long orderId);
	
	/**
	 * ����ȯ�ⶳ�ӿ�
	 * @param userId 	�û�ID	���ǿգ�
	 * @param couponId 	����ȯID	���ǿգ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00012", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreeze(final long userId,final long couponId);
	
	/**
	 * ����ȯ�ⶳ�ӿ�,�������¶����ȯ�ⶳ
	 * @param userId 	�û�ID	���ǿգ�
	 * @param orderId 	����ID	���ǿգ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00013", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreezeByOrderId(final long userId,final long orderId);
	
	/**
	 * ����ȯʹ�ýӿ�
	 * @param userId 	�û�ID	���ǿգ�
	 * @param couponId 	����ȯID	���ǿգ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00014", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUsed(final long userId,final long couponId);
	
	/**
	 * ��������ȯ���Ͻӿ�
	 * @param userId 	�û�ID	���ǿգ�
	 * @param couponId 	����ȯID	���ǿգ�
	 * @param invalidAdminId	���ϲ�����ID	��������գ�
	 * @param invalidReason		��������		��������գ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00015", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancel(final long userId,final long couponId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * ����ȯ���Ͻӿ�,���ݶ���ID
	 * @param userId 	�û�ID	���ǿգ�
	 * @param orderId 	����ID	���ǿգ�
	 * @param invalidAdminId	���ϲ�����ID	��������գ�
	 * @param invalidReason		��������		��������գ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00016", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancelByOrderId(final long userId,final long orderId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * ����ȯ�˿�ӿ�,���ݶ���ID
	 * @param userId 	�û�ID	���ǿգ�
	 * @param orderId 	����ID	���ǿգ�
	 * @param invalidAdminId	���ϲ�����ID	��������գ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00017", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponRefundByOrderId(final long userId,final long orderId,
			final long invalidAdminId);
	
	/**
	 * ����ȯ�������Ͻӿ�
	 * @param userId 	�û�ID	���ǿգ�
	 * @param couponId 	����ȯID	���ǿգ�
	 * @param invalidAdminId	���ϲ�����ID	��������գ�
	 * @param invalidReason		��������		��������գ�
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00018", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponBatchCancel(
			final long batchId,final long invalidAdminId,final String invalidReason);
}