package com.tuan.pmt.domain.repository;

import java.util.List;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.core.common.page.PageList;
import com.tuan.pmt.dao.CpnCouponBatchCatDAO;
import com.tuan.pmt.dao.CpnCouponBatchCityDAO;
import com.tuan.pmt.dao.CpnCouponBatchDAO;
import com.tuan.pmt.dao.CpnCouponDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;

public class CouponQueryDomainRepository {
	private CpnCouponDAO cpnCouponDAO ;
	private CpnCouponBatchDAO cpnCouponBatchDAO;
	private SpyMemcachedClient youhuiSpyMemcachedClient;
	private CpnCouponBatchCityDAO cpnCouponBatchCityDAO;
	private CpnCouponBatchCatDAO cpnCouponBatchCatDAO;
	
	/**
	 * �������α�ID����ѯ���α����
	 * @param batchId	���α�ID
	 * @return	���ζ���
	 */
	public CpnCouponBatchDO queryCouponBatchByBatchId(long batchId){ 
		return cpnCouponBatchDAO.queryCpnCouponBatchByBatchId(batchId);
	}
	
	/**
	 * ��������ID����ȡ�������Ƴ��е��б�
	 * @param batchId	����ID
	 * @return	List<CpnCouponBatchCityDO> ���Ƴ��е��б����
	 */
	public List<CpnCouponBatchCityDO> queryBatchCityListByBatchId(long batchId){
		return cpnCouponBatchCityDAO.queryCpnCouponBatchCityByBatchId(batchId);
	}
	
	/**
	 * ��������ID����ȡ�������Ʒ�����б�
	 * @param batchId	����ID
	 * @return	List<CpnCouponBatchCatDO> ���Ʒ�����б����
	 */
	public List<CpnCouponBatchCatDO> queryBatchCatListByBatchId(long batchId){
		return cpnCouponBatchCatDAO.queryCpnCouponBatchCatByBatchId(batchId);
	}
	
	/**
	 * ���ݴ�����û�ID��ȯ״̬�б���ҳ��ѯ�û��µĴ���ȯ��
	 * @param userId		�û�ID
	 * @param statusList	״̬�б�
	 * @param platType 		���÷�Χ���ֻ��ˡ���վ��
	 * @param pageSize 		ÿҳ��¼��
	 * @param currPage 		��ǰҳ��
	 * @return				����ȯ�б�
	 */
	public PageList queryCpnCouponByUserId(final long userId,final List<Integer> statusList, 
			String platType, int currPage, int pageSize){
		return cpnCouponDAO.queryCpnCouponByUserId(userId,statusList, platType, currPage,  pageSize);
	}
	
	/**
	 * ͨ������ȯ���룬��ѯ����ȯ����
	 * @param code	����ȯ����
	 * @return	����ȯ����
	 */
	public CpnCouponDO queryCpnCouponDOByCode(String code){
		return cpnCouponDAO.queryCpnCouponByCode(code);
	}
	
	/**
	 * ���ݴ���ȯID����ѯ����ȯ����
	 * @param couponId	����ȯID
	 * @return	����ȯ����
	 */
	public CpnCouponDO queryCouponDoByCouponId(final long couponId){
		return cpnCouponDAO.queryCpnCouponByCouponId(couponId);
	}
	
	/**
	 * ���ݶ���ID����ѯ����ȯ�����б�
	 * @param orderId	����ID
	 * @return	����ȯ�����б�
	 */
	public List<CpnCouponDO> queryCouponDOByOrderId(final long orderId){
		return cpnCouponDAO.queryCpnCouponByOrderId(orderId);
	}
	
	public List<CpnCouponDO> queryCouponDOByOrderId(final long orderId,final long userId,final List<Integer> statusList){
		return cpnCouponDAO.queryCouponDOByOrderIdAndStatus(orderId,userId, statusList);
	}
	
	/**
     * ���ݴ�����û�ID������id����ѯ�û��µĴ���ȯ��
     * @param userId        �û�ID
     * @param batchId       ����id
     * @return              ����ȯ�б�
     */
    public List<CpnCouponDO> queryCouponsByUserIdAndBatchId(final long userId,final long batchId){
    	return cpnCouponDAO.queryCouponsByUserIdAndBatchId(userId, batchId);
    }
	
	public CpnCouponDAO getCpnCouponDAO() {
		return cpnCouponDAO;
	}
	public void setCpnCouponDAO(CpnCouponDAO cpnCouponDAO) {
		this.cpnCouponDAO = cpnCouponDAO;
	}
	public CpnCouponBatchDAO getCpnCouponBatchDAO() {
		return cpnCouponBatchDAO;
	}
	public void setCpnCouponBatchDAO(CpnCouponBatchDAO cpnCouponBatchDAO) {
		this.cpnCouponBatchDAO = cpnCouponBatchDAO;
	}
	public SpyMemcachedClient getYouhuiSpyMemcachedClient() {
		return youhuiSpyMemcachedClient;
	}
	public void setYouhuiSpyMemcachedClient(SpyMemcachedClient youhuiSpyMemcachedClient) {
		this.youhuiSpyMemcachedClient = youhuiSpyMemcachedClient;
	}
	public CpnCouponBatchCityDAO getCpnCouponBatchCityDAO() {
		return cpnCouponBatchCityDAO;
	}
	public void setCpnCouponBatchCityDAO(CpnCouponBatchCityDAO cpnCouponBatchCityDAO) {
		this.cpnCouponBatchCityDAO = cpnCouponBatchCityDAO;
	}
	public CpnCouponBatchCatDAO getCpnCouponBatchCatDAO() {
		return cpnCouponBatchCatDAO;
	}
	public void setCpnCouponBatchCatDAO(CpnCouponBatchCatDAO cpnCouponBatchCatDAO) {
		this.cpnCouponBatchCatDAO = cpnCouponBatchCatDAO;
	}
}
