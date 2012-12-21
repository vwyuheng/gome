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
 * ����ȯ��ѯ�ӿڶ���
 * @author duandj
 *
 */
public interface CouponQueryService {

	/**
	 * ���ݴ�����û�ID��ȯ״̬�б���ѯ�û��µĴ���ȯ��
	 * @param userId �û�ID���ǿգ�
	 * @param statusList	ֵ���� com.tuan.pmt.model.constant.status.CouponStatusEnum �գ� �����û��µ����д���ȯ   
	 * @param platType   ƽ̨����
	 * @param currPage ��ǰ�ڼ�ҳ
	 * @param pageSize ÿҳ��ʾ����
	 * @return CallResult<CouponQueryResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult
	 * @see	CouponQueryResult
	 * @see PageList   
	 */
	@ProductCode(code = "00000", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(final long userId,final List<Integer> statusList,final String platType,final int currPage,final int pageSize);
	
	/**
	 * ���ݴ���Ĵ���ȯID����ѯ����ȯ�����ε���ϸ��Ϣ��
	 * @param userId �û�ID���ǿգ�
	 * @param statusList	�գ� �����û��µ����д���ȯ
	 * @return CallResult<CouponQueryResult> �ӿڵ��ý������
	 * 
	 * @see	CallResult   businessObject ����ȯ������Ϣ
	 * @see	CouponQueryResult -- ����ȯ��Ϣ
	 * @see CpnCouponModel 
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(final long couponId);
	
	
	/**
     * ��������id��ѯ������Ϣ
     * @param batchId	����id
     * @return	CallResult<CouponQueryResult> �ӿڵ��ý������
     * 
     * @see CallResult   businessObject ����ȯ������Ϣ
     * @see CouponQueryResult -- ����ȯ��Ϣ
     * @see CpnCouponBatchModel -- ������Ϣ
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
     * @see CpnCouponModel -- ����ȯģ�Ͷ���
     */
    @ProductCode(code = "00003", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(final String couponCode);
    
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
    @ProductCode(code = "00004", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
    CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(final long userId,final long orderId,final List<Integer> statusList);
}
