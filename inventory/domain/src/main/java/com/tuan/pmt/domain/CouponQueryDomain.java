package com.tuan.pmt.domain;

import java.util.ArrayList;
import java.util.List;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.core.common.page.PageList;
import com.tuan.core.common.page.Paginator;
import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.support.util.ModelConverter;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.cache.CacheKeyEnum;

/**
 * ����ȯ��ѯ������
 * @author duandj
 *
 */
public class CouponQueryDomain {
	CpnCouponBatchCityDO cpnCouponBatchCityDO;
	CpnCouponBatchCatDO	CpnCouponBatchCatDO;
	CpnCouponDO cpnCouponDO;
	CpnCouponBatchDO cpnCouponBatchDO;
	CouponQueryDomainRepository couponQueryDomainRepository;
	private SpyMemcachedClient youhuiSpyMemcachedClient;
	
	/**
	 * ��������ȯģ�Ͷ���
	 * @return	����ȯģ�Ͷ���
	 */
	public CpnCouponModel makeCouponModel(){
		if(cpnCouponDO == null){
			return null;
		}
    	CpnCouponBatchDO cpnCouponBatchDO = queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCityDO> cityIdList = queryBatchCityListByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCatDO> catIdList = queryBatchCatListByBatchId(cpnCouponDO.getBatchId());
        return ModelConverter.toCouponModel(cpnCouponDO, cpnCouponBatchDO,catIdList, cityIdList);
	}
	
	/**
	 * ��������ģ�Ͷ���
	 * @param batchId ����ID
	 * @return	����ģ�Ͷ���
	 */
	public CpnCouponBatchModel makeBatchModel(long batchId){
    	List<CpnCouponBatchCityDO> cityIdList = queryBatchCityListByBatchId(batchId);
    	List<CpnCouponBatchCatDO> catIdList = queryBatchCatListByBatchId(batchId);
        return ModelConverter.toModelFromCpnCouponBatchDO(cpnCouponBatchDO,catIdList, cityIdList);
	}
	
	/**
     * ���ݴ�����û�ID������id����ѯ�û��µĴ���ȯ��
     * @param userId        �û�ID
     * @param batchId       ����id
     * @return              ����ȯ�б�
     */
    public List<CpnCouponModel> queryCouponsByUserIdAndBatchId(final long userId,final long batchId){
    	List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponsByUserIdAndBatchId(userId, batchId);
		if(cpnCouponDOList == null){
			return null;
		}
		List<CpnCouponModel> modelList = new ArrayList<CpnCouponModel>();
		for(CpnCouponDO cpnCouponDO : cpnCouponDOList){
			CpnCouponBatchDO cpnCouponBatchDO = queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
	    	List<CpnCouponBatchCityDO> cityIdList = queryBatchCityListByBatchId(cpnCouponDO.getBatchId());
	    	List<CpnCouponBatchCatDO> catIdList = queryBatchCatListByBatchId(cpnCouponDO.getBatchId());
	    	modelList.add(ModelConverter.toCouponModel(cpnCouponDO, cpnCouponBatchDO,catIdList, cityIdList));
		}
    	return modelList;
    }
    
    /**
     * ͳ�ƴ���ȯ�����������û�ID������ID
     * @param userId	�û�ID
     * @param batchId	����ID
     * @return	����ȯ����
     */
    public int queryCouponCountByUserIdAndBatchId(final long userId,final long batchId){
    	List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponsByUserIdAndBatchId(userId, batchId);
    	if(cpnCouponDOList != null){
    		return cpnCouponDOList.size();
    	}else{
    		return 0;
    	}
    }
	
	/**
	 * ���ݴ�����û�ID��ȯ״̬�б���ѯ�û��µĴ���ȯ��
	 * @param userId		�û�ID
	 * @param statusList	״̬�б�
	 * @param platType 		���÷�Χ���ֻ��ˡ���վ��
	 * @param pageSize 		ÿҳ��¼��
	 * @param currPage 		��ǰҳ��
	 * @return				����ȯ�б�
	 */
	public PageList queryCouponsByUserId(
			final long userId,final List<Integer> statusList, String platType, int currPage, int pageSize){
		 PageList pageList =  couponQueryDomainRepository.queryCpnCouponByUserId(
				 userId,statusList, platType, currPage,  pageSize);
	        if(null != pageList && ! pageList.isEmpty()){
	                PageList returnPage = new PageList();
	                Paginator paginator = pageList.getPaginator();
	                returnPage.setPaginator(paginator);
	                for(Object obj : pageList){
	                	CpnCouponDO cpnCouponDO = (CpnCouponDO)obj;
	                	CpnCouponBatchDO cpnCouponBatchDO = queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
	                	List<CpnCouponBatchCityDO> cityIdList = queryBatchCityListByBatchId(cpnCouponDO.getBatchId());
	                	List<CpnCouponBatchCatDO> catIdList = queryBatchCatListByBatchId(cpnCouponDO.getBatchId());
	                    returnPage.add(ModelConverter.toCouponModel(cpnCouponDO, cpnCouponBatchDO,
	                    		catIdList, cityIdList));
	                }
	            return returnPage;
	        }
	        return null;
	}
	
	/**
	 * ��������ID����ȡ�������Ʒ�����б�
	 * @param batchId	����ID
	 * @return	List<CpnCouponBatchCatDO> ���Ʒ�����б����
	 */
	public List<CpnCouponBatchCatDO> queryBatchCatListByBatchId(long batchId){
		String cacheCatKey = CacheKeyEnum.CACHE_CAT_KEY.getName() + batchId;
		List<CpnCouponBatchCatDO> cpnCouponBatchCatDOList = youhuiSpyMemcachedClient.get(cacheCatKey);
		if(cpnCouponBatchCatDOList == null){
			cpnCouponBatchCatDOList = couponQueryDomainRepository.queryBatchCatListByBatchId(batchId);
			youhuiSpyMemcachedClient.set(cacheCatKey, cpnCouponBatchCatDOList);
		}
		if(cpnCouponBatchCatDOList == null){
			cpnCouponBatchCatDOList = new ArrayList<CpnCouponBatchCatDO>();
		}
		return cpnCouponBatchCatDOList;
	}
	
	/**
	 * �������α�ID����ѯ���α����
	 * @param batchId	���α�ID
	 * @return	���ζ���
	 */
	public CpnCouponBatchDO queryCouponBatchByBatchId(long batchId){ 
		String cacheBatchKey = CacheKeyEnum.CACHE_BATCH_KEY.getName() + batchId;
		CpnCouponBatchDO cpnCouponBatchDO = youhuiSpyMemcachedClient.get(cacheBatchKey);
		if(cpnCouponBatchDO == null){
			cpnCouponBatchDO = couponQueryDomainRepository.queryCouponBatchByBatchId(batchId);
			youhuiSpyMemcachedClient.set(cacheBatchKey, cpnCouponBatchDO);
		}
		return cpnCouponBatchDO;
	}
	
	/**
	 * ��������ID����ȡ�������Ƴ��е��б�
	 * @param batchId	����ID
	 * @return	List<CpnCouponBatchCityDO> ���Ƴ��е��б����
	 */
	public List<CpnCouponBatchCityDO> queryBatchCityListByBatchId(long batchId){
		String cacheCityKey = CacheKeyEnum.CACHE_CITY_KEY.getName() + batchId;
		List<CpnCouponBatchCityDO> cpnCouponBatchCityDOList = youhuiSpyMemcachedClient.get(cacheCityKey);
		if(cpnCouponBatchCityDOList == null){
			cpnCouponBatchCityDOList = couponQueryDomainRepository.queryBatchCityListByBatchId(batchId);
			youhuiSpyMemcachedClient.set(cacheCityKey, cpnCouponBatchCityDOList);
		}
		if(cpnCouponBatchCityDOList == null){
			cpnCouponBatchCityDOList = new ArrayList<CpnCouponBatchCityDO>();
		}
		return cpnCouponBatchCityDOList;
	}
	
	
	public CpnCouponBatchCityDO getCpnCouponBatchCityDO() {
		return cpnCouponBatchCityDO;
	}
	public void setCpnCouponBatchCityDO(CpnCouponBatchCityDO cpnCouponBatchCityDO) {
		this.cpnCouponBatchCityDO = cpnCouponBatchCityDO;
	}
	public CpnCouponBatchCatDO getCpnCouponBatchCatDO() {
		return CpnCouponBatchCatDO;
	}
	public void setCpnCouponBatchCatDO(CpnCouponBatchCatDO cpnCouponBatchCatDO) {
		CpnCouponBatchCatDO = cpnCouponBatchCatDO;
	}
	public CpnCouponDO getCpnCouponDO() {
		return cpnCouponDO;
	}
	public void setCpnCouponDO(CpnCouponDO cpnCouponDO) {
		this.cpnCouponDO = cpnCouponDO;
	}
	public CpnCouponBatchDO getCpnCouponBatchDO() {
		return cpnCouponBatchDO;
	}
	public void setCpnCouponBatchDO(CpnCouponBatchDO cpnCouponBatchDO) {
		this.cpnCouponBatchDO = cpnCouponBatchDO;
	}
	public CouponQueryDomainRepository getCouponQueryDomainRepository() {
		return couponQueryDomainRepository;
	}
	public void setCouponQueryDomainRepository(CouponQueryDomainRepository couponQueryDomainRepository) {
		this.couponQueryDomainRepository = couponQueryDomainRepository;
	}

	public SpyMemcachedClient getYouhuiSpyMemcachedClient() {
		return youhuiSpyMemcachedClient;
	}

	public void setYouhuiSpyMemcachedClient(SpyMemcachedClient youhuiSpyMemcachedClient) {
		this.youhuiSpyMemcachedClient = youhuiSpyMemcachedClient;
	}
}
