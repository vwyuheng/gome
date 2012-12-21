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
 * 代金券查询操作类
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
	 * 制作代金券模型对象
	 * @return	代金券模型对象
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
	 * 制作批次模型对象
	 * @param batchId 批次ID
	 * @return	批次模型对象
	 */
	public CpnCouponBatchModel makeBatchModel(long batchId){
    	List<CpnCouponBatchCityDO> cityIdList = queryBatchCityListByBatchId(batchId);
    	List<CpnCouponBatchCatDO> catIdList = queryBatchCatListByBatchId(batchId);
        return ModelConverter.toModelFromCpnCouponBatchDO(cpnCouponBatchDO,catIdList, cityIdList);
	}
	
	/**
     * 根据传入的用户ID和批次id，查询用户下的代金券。
     * @param userId        用户ID
     * @param batchId       批次id
     * @return              代金券列表
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
     * 统计代金券个数，根据用户ID和批次ID
     * @param userId	用户ID
     * @param batchId	批次ID
     * @return	代金券个数
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
	 * 根据传入的用户ID和券状态列表，查询用户下的代金券。
	 * @param userId		用户ID
	 * @param statusList	状态列表
	 * @param platType 		适用范围：手机端、网站端
	 * @param pageSize 		每页记录数
	 * @param currPage 		当前页号
	 * @return				代金券列表
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
	 * 根据批次ID，获取批次限制分类的列表
	 * @param batchId	批次ID
	 * @return	List<CpnCouponBatchCatDO> 限制分类的列表对象
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
	 * 根据批次表ID，查询批次表对象
	 * @param batchId	批次表ID
	 * @return	批次对象
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
	 * 根据批次ID，获取批次限制城市的列表
	 * @param batchId	批次ID
	 * @return	List<CpnCouponBatchCityDO> 限制城市的列表对象
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
