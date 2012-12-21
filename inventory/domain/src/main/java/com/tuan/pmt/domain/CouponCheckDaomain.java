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
 * 代金券验证操作类
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
	 * 校验代金券是否可以用在某个商品上
	 * @param couponCheckDaomain
	 * @return
	 */
	public CouponCheckResEnum checkCoupon(){
		//校验用户
		if(cpnCouponDO.getUserId() != null && userId != cpnCouponDO.getUserId()){
			return CouponCheckResEnum.INVALID_USER;
		}
		CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
		if(cpnCouponBatchDO == null){
			return CouponCheckResEnum.INVALID_BATCHID;
		}
		//校验批次状态
		if(cpnCouponBatchDO.getStatus() == null || 
				cpnCouponBatchDO.getStatus().intValue() != CouponBatchStatusEnum.CREATED.getCode()){
			return CouponCheckResEnum.INVALID_STATUS;
		}
		//校验代金券状态
		if(cpnCouponDO.getStatus() == null || cpnCouponDO.getStatus() != CouponStatusEnum.UNUSED.getCode()){
			return CouponCheckResEnum.INVALID_STATUS;
		}
		//校验有效期
		if(cpnCouponBatchDO.getStartTime() == null || cpnCouponBatchDO.getEndTime() == null || 
				DateUtils.checkTimeScope(DateUtils.getNowTimestamp(),cpnCouponBatchDO.getStartTime(), 
				cpnCouponBatchDO.getEndTime()) != 0){
			return CouponCheckResEnum.INVALID_TIME;
		}
		//校验订单来源
		if(cpnCouponBatchDO.getUseTerminal() == null || cpnCouponBatchDO.getUseTerminal().indexOf(orderFrom) == -1){
			return CouponCheckResEnum.INVALID_ORDERFROM;
		}
		//校验商品单价
		if(goodsInfoParam.getGoodsPrice() == null ||
				goodsInfoParam.getGoodsPrice().floatValue() <= cpnCouponBatchDO.getGoodsPrice().floatValue()){
			return CouponCheckResEnum.INVALID_GOODSPRICE;
		}else{
			//校验商品集合金额
			if(goodsInfoParam.getGoodsAmount() == null || 
					goodsInfoParam.getGoodsAmount().floatValue() <= cpnCouponBatchDO.getGoodsSetAmount().floatValue()){
				return CouponCheckResEnum.INVALID_GOODSAMOUNT;
			}
		}
		//校验商品ID
		String goodsIds = cpnCouponBatchDO.getGoodsIds();
		if(goodsInfoParam.getGoodsId() <= 0){
			return CouponCheckResEnum.INVALID_GOODSID;
		}
		if(goodsIds != null && !goodsIds.equals("")){
			if(goodsIds.indexOf(goodsInfoParam.getGoodsId() + "") == -1){
				return CouponCheckResEnum.GOODSID_NOT_USED;
			}
		}
		//校验城市
		if(cpnCouponBatchDO.getAllCity() == 0 && !checkCity(cpnCouponBatchDO.getBatchId(),goodsInfoParam.getCityId())){
			return CouponCheckResEnum.INVALID_CITY;
		}
		//校验分类
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
