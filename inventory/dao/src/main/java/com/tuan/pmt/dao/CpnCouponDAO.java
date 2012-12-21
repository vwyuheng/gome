package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.core.common.page.PageList;
import com.tuan.pmt.dao.data.CpnCouponDO;


public interface CpnCouponDAO {
	/**
	 * �޸Ĵ���ȯ�����ݴ���ȯid
	 * @param cpnCouponDO 
	 * @return Integer �޸ĳɹ������޸����������򷵻�0
	 */
	public Integer update(CpnCouponDO cpnCouponDO);
	
	/**
	 * ���ݶ���ID����ѯ����ȯ�б�
	 * @param orderId
	 * @return	����ȯ�����б� ��ʧ�ܻ��޼�¼����null
	 */
	public List<CpnCouponDO> queryCpnCouponByOrderId(final long orderId) ;
	
	/**
	 * ���ݶ���ID����ѯ����ȯ�б�
	 * @param orderId	����ID
	 * @param userId	�û�ID
	 * @param statusList	״̬ID ��null:������״̬
	 * @return	����ȯ�����б� ��ʧ�ܻ��޼�¼����null
	 */
	public List<CpnCouponDO> queryCouponDOByOrderIdAndStatus(final long orderId,final long userId,final List<Integer> statusList);
	
	/**
	 * ���ݴ���ȯID����ѯ����ȯ
	 * @param couponId
	 * @return	����ȯ����ʧ�ܻ��޼�¼����null
	 */
	public CpnCouponDO queryCpnCouponByCouponId(final long couponId) ;
	
	/**
	 * ���ݴ�����û�ID��ȯ״̬�б���ѯ�û��µĴ���ȯ��
	 * @param userId		�û�ID
	 * @param statusList	Ҫ��ѯ��״̬�б�
	 * @param pageSize 
	 * @param currPage 
	 * @param platType 
	 * @return
	 */
	public PageList queryCpnCouponByUserId(final long userId,final List<Integer> statusList, String platType, int currPage, int pageSize);
	
	/**
	 * ������֤�룬��ѯ����ȯ
	 * @param code	��֤��
	 * @return	����ȯ����
	 */
	public CpnCouponDO queryCpnCouponByCode(String code);

    /**
     * �����û�id,����id��ѯ����ȯ�б�
     * @param userId
     * @param batchId
     * @return
     */
    public List<CpnCouponDO> queryCouponsByUserIdAndBatchId(long userId,
            long batchId);
}
