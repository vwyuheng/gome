package com.tuan.pmt.domain;

import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.support.util.DateUtils;
import com.tuan.pmt.model.constant.res.CouponCheckResEnum;
import com.tuan.pmt.model.constant.status.CouponBatchStatusEnum;
import com.tuan.pmt.model.constant.status.CouponStatusEnum;
import com.tuan.pmt.model.param.GoodsInfoParam;

/**
 * ����ȯ��֤������
 * @author duandj
 *
 */
public class CouponCheckDaomain {
	private GoodsInfoParam goodsInfoParam;
	private long couponId;
	private long userId;
	private String orderFrom;
	private CpnCouponDO cpnCouponDO;
	private CpnCouponBatchDO cpnCouponBatchDO;
	private CouponQueryDomain couponQueryDomain;
	private CouponQueryDomainRepository couponQueryDomainRepository;
	
	/**
	 * У�����ȯ�Ƿ��������ĳ����Ʒ��
	 * @param couponCheckDaomain
	 * @return
	 */
	public CouponCheckResEnum checkCoupon(){
		//У���û�
		if(cpnCouponDO.getUserId() != null && userId != cpnCouponDO.getUserId()){
			return CouponCheckResEnum.INVALID_USER;
		}
		CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
		if(cpnCouponBatchDO == null){
			return CouponCheckResEnum.INVALID_BATCHID;
		}
		//У������״̬
		if(cpnCouponBatchDO.getStatus() == null || 
				cpnCouponBatchDO.getStatus().intValue() != CouponBatchStatusEnum.CREATED.getCode()){
			return CouponCheckResEnum.INVALID_STATUS;
		}
		//У�����ȯ״̬
		if(cpnCouponDO.getStatus() == null || cpnCouponDO.getStatus() != CouponStatusEnum.UNUSED.getCode()){
			return CouponCheckResEnum.INVALID_STATUS;
		}
		//У����Ч��
		if(cpnCouponBatchDO.getStartTime() == null || cpnCouponBatchDO.getEndTime() == null || 
				DateUtils.checkTimeScope(DateUtils.getNowTimestamp(),cpnCouponBatchDO.getStartTime(), 
				cpnCouponBatchDO.getEndTime()) != 0){
			return CouponCheckResEnum.INVALID_TIME;
		}
		//У�鶩����Դ
		if(cpnCouponBatchDO.getUseTerminal() == null || cpnCouponBatchDO.getUseTerminal().indexOf(orderFrom) == -1){
			return CouponCheckResEnum.INVALID_ORDERFROM;
		}
		//У����Ʒ����
		if(goodsInfoParam.getGoodsPrice() == null ||
				goodsInfoParam.getGoodsPrice().floatValue() <= cpnCouponBatchDO.getGoodsPrice().floatValue()){
			return CouponCheckResEnum.INVALID_GOODSPRICE;
		}else{
			//У����Ʒ���Ͻ��
			if(goodsInfoParam.getGoodsAmount() == null || 
					goodsInfoParam.getGoodsAmount().floatValue() <= cpnCouponBatchDO.getGoodsSetAmount().floatValue()){
				return CouponCheckResEnum.INVALID_GOODSAMOUNT;
			}
		}
		//У����ƷID
		String goodsIds = cpnCouponBatchDO.getGoodsIds();
		if(goodsInfoParam.getGoodsId() <= 0){
			return CouponCheckResEnum.INVALID_GOODSID;
		}
		if(goodsIds != null && !goodsIds.equals("")){
			if(goodsIds.indexOf(goodsInfoParam.getGoodsId() + "") == -1){
				return CouponCheckResEnum.GOODSID_NOT_USED;
			}
		}
		//У�����
		if(cpnCouponBatchDO.getAllCity() == 0 && !checkCity(cpnCouponBatchDO.getBatchId(),goodsInfoParam.getCityId())){
			return CouponCheckResEnum.INVALID_CITY;
		}
		//У�����
		if(cpnCouponBatchDO.getAllCat() == 0 && !checkCat(cpnCouponBatchDO.getBatchId(),goodsInfoParam.getSecondCatId())){
			return CouponCheckResEnum.INVALID_CAT;
		}
		return CouponCheckResEnum.SUCCESS;
	}
	
	private boolean checkCity(long batchId, long cityId){
		List<CpnCouponBatchCityDO> cityList = couponQueryDomain.queryBatchCityListByBatchId(batchId);
		for(CpnCouponBatchCityDO cpnCouponBatchCityDO : cityList){
			if(cpnCouponBatchCityDO.getCityId() == cityId){
				return true;
			}
		}
		return false;
	}
	private boolean checkCat(long batchId, long catId){
		List<CpnCouponBatchCatDO> catList = couponQueryDomain.queryBatchCatListByBatchId(batchId);
		for(CpnCouponBatchCatDO cpnCouponBatchCatDO : catList){
			if(cpnCouponBatchCatDO.getCatId() == catId){
				return true;
			}
		}
		return false;
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
	public GoodsInfoParam getGoodsInfoParam() {
		return goodsInfoParam;
	}
	public void setGoodsInfoParam(GoodsInfoParam goodsInfoParam) {
		this.goodsInfoParam = goodsInfoParam;
	}
	public long getCouponId() {
		return couponId;
	}
	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getOrderFrom() {
		return orderFrom;
	}
	public void setOrderFrom(String orderFrom) {
		this.orderFrom = orderFrom;
	}
	public CouponQueryDomainRepository getCouponQueryDomainRepository() {
		return couponQueryDomainRepository;
	}
	public void setCouponQueryDomainRepository(CouponQueryDomainRepository couponQueryDomainRepository) {
		this.couponQueryDomainRepository = couponQueryDomainRepository;
	}
	public CouponQueryDomain getCouponQueryDomain() {
		return couponQueryDomain;
	}
	public void setCouponQueryDomain(CouponQueryDomain couponQueryDomain) {
		this.couponQueryDomain = couponQueryDomain;
	}
}
