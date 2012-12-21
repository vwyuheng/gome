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
	
	/**
     * ���ݴ�����û�ID��ȯ״̬�б���ѯ�û��µĴ���ȯ��
     * @param userId �û�ID���ǿգ�
     * @param statusList    ֵ���� com.tuan.pmt.model.constant.status.CouponStatusEnum �գ� �����û��µ����д���ȯ   
     * @param platType   ƽ̨����
     * @param currPage ��ǰ�ڼ�ҳ
     * @param pageSize ÿҳ��ʾ����
     * @return CallResult<CouponQueryResult> �ӿڵ��ý������
     * 
     * @see CallResult
     * @see CouponQueryResult
     * @see CpnCouponModel   
     */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(final long userId,final List<Integer> statusList,final String platType ,final int currPage,final int pageSize);
	
	/**
	 * ���ݴ���Ĵ���ȯID����ѯ����ȯ�����ε���ϸ��Ϣ��
	 * @param userId �û�ID���ǿգ�
	 * @param statusList	�գ� �����û��µ����д���ȯ
	 * @return CallResult<CouponQueryResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult   businessObject ����ȯ������Ϣ
	 * @see	CouponQueryResult -- ����ȯ��Ϣ
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(final long couponId);
	
	
	/**
     * ��������id��ѯ������Ϣ
     * @param batchId
     * @return
     */
    @ProductCode(code = "00002", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponBatchModel>> queryCouponBatchByBatchId(final long batchId);
    
    /**
     * ���ݴ���Ĵ���ȯ��֤�룬��ѯ����ȯ�������ε���ϸ��Ϣ��
     * @param userId �û�ID���ǿգ�
     * @param statusList    �գ� �����û��µ����д���ȯ
     * @return CallResult<CouponQueryResult> �ӿڵ��ý������
     * 
     * @see CallResult   businessObject ����ȯ������Ϣ
     * @see CouponQueryResult -- ����ȯ��Ϣ
     */
    @ProductCode(code = "00003", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(final String couponCode);
	
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
	 * ����ȯ����ӿ�
	 * @param userId 	�û�ID
	 * @param couponId 	����ȯID
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00011", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponFreeze(final long userId,final long couponId,final long orderId);
	
	/**
	 * ����ȯ�ⶳ�ӿ�
	 * @param userId 	�û�ID
	 * @param couponId 	����ȯID
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00012", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUnFreeze(final long userId,final long couponId);
	
	/**
	 * ����ȯʹ�ýӿ�
	 * @param userId 	�û�ID
	 * @param couponId 	����ȯID
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00013", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponUsed(final long userId,final long couponId);
	
	/**
	 * ��������ȯ���Ͻӿ�
	 * @param userId 	�û�ID
	 * @param couponId 	����ȯID
	 * @param invalidAdminId	���ϲ�����ID
	 * @param invalidReason		��������
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00014", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancel(final long userId,final long couponId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * ����ȯ���Ͻӿ�,���ݶ���ID
	 * @param userId 	�û�ID
	 * @param orderId 	����ID
	 * @param invalidAdminId	���ϲ�����ID
	 * @param invalidReason		��������
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00016", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponCancelByOrderId(final long userId,final long orderId,
			final long invalidAdminId,final String invalidReason);
	
	/**
	 * ����ȯ�������Ͻӿ�
	 * @param userId 	�û�ID
	 * @param couponId 	����ȯID
	 * @param invalidAdminId	���ϲ�����ID
	 * @param invalidReason		��������
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	@ProductCode(code = "00015", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponUpdateResult> couponBatchCancel(
			final long batchId,final long invalidAdminId,final String invalidReason);
	
	/**
	 * ����ȯ�ⶳ�ӿ�,�������¶����ȯ�ⶳ
	 * @param userId 	�û�ID
	 * @param orderId 	����ID
	 * @return CallResult<CouponUpdateResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponUpdateResult
	 */
	CallResult<CouponUpdateResult> couponUnFreezeByOrderId(final long userId,final long orderId);
	
	/**
     * ���ݶ���ID���û�ID��״̬�б���ѯ����ȯ�������ε���ϸ��Ϣ��
     * @param userId 	�û�ID���ǿգ�
     * @param orderId	����ID���ǿգ�   
     * @param statusList    �գ� ������״̬
     * @return CallResult<CouponQueryResult> �ӿڵ��ý������
     * 
     * @see CallResult   businessObject ����ȯ������Ϣ
     * @see CouponQueryResult -- ����ȯ��Ϣ
     * @see CpnCouponModel -- ����ȯģ�Ͷ���
     */
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(final long userId,final long orderId,final List<Integer> statusList);

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
	CallResult<CouponUpdateResult> couponRefundByOrderId(final long userId,final long orderId,final long invalidAdminId);
}
