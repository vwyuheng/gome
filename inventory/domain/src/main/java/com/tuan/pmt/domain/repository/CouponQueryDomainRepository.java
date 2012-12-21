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
	 * 根据批次表ID，查询批次表对象
	 * @param batchId	批次表ID
	 * @return	批次对象
	 */
	public CpnCouponBatchDO queryCouponBatchByBatchId(long batchId){ 
		return cpnCouponBatchDAO.queryCpnCouponBatchByBatchId(batchId);
	}
	
	/**
	 * 根据批次ID，获取批次限制城市的列表
	 * @param batchId	批次ID
	 * @return	List<CpnCouponBatchCityDO> 限制城市的列表对象
	 */
	public List<CpnCouponBatchCityDO> queryBatchCityListByBatchId(long batchId){
		return cpnCouponBatchCityDAO.queryCpnCouponBatchCityByBatchId(batchId);
	}
	
	/**
	 * 根据批次ID，获取批次限制分类的列表
	 * @param batchId	批次ID
	 * @return	List<CpnCouponBatchCatDO> 限制分类的列表对象
	 */
	public List<CpnCouponBatchCatDO> queryBatchCatListByBatchId(long batchId){
		return cpnCouponBatchCatDAO.queryCpnCouponBatchCatByBatchId(batchId);
	}
	
	/**
	 * 根据传入的用户ID和券状态列表，分页查询用户下的代金券。
	 * @param userId		用户ID
	 * @param statusList	状态列表
	 * @param platType 		适用范围：手机端、网站端
	 * @param pageSize 		每页记录数
	 * @param currPage 		当前页号
	 * @return				代金券列表
	 */
	public PageList queryCpnCouponByUserId(final long userId,final List<Integer> statusList, 
			String platType, int currPage, int pageSize){
		return cpnCouponDAO.queryCpnCouponByUserId(userId,statusList, platType, currPage,  pageSize);
	}
	
	/**
	 * 通过代金券密码，查询代金券对象
	 * @param code	代金券密码
	 * @return	代金券对象
	 */
	public CpnCouponDO queryCpnCouponDOByCode(String code){
		return cpnCouponDAO.queryCpnCouponByCode(code);
	}
	
	/**
	 * 根据代金券ID，查询代金券对象
	 * @param couponId	代金券ID
	 * @return	代金券对象
	 */
	public CpnCouponDO queryCouponDoByCouponId(final long couponId){
		return cpnCouponDAO.queryCpnCouponByCouponId(couponId);
	}
	
	/**
	 * 根据订单ID，查询代金券对象列表
	 * @param orderId	订单ID
	 * @return	代金券对象列表
	 */
	public List<CpnCouponDO> queryCouponDOByOrderId(final long orderId){
		return cpnCouponDAO.queryCpnCouponByOrderId(orderId);
	}
	
	public List<CpnCouponDO> queryCouponDOByOrderId(final long orderId,final long userId,final List<Integer> statusList){
		return cpnCouponDAO.queryCouponDOByOrderIdAndStatus(orderId,userId, statusList);
	}
	
	/**
     * 根据传入的用户ID和批次id，查询用户下的代金券。
     * @param userId        用户ID
     * @param batchId       批次id
     * @return              代金券列表
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
