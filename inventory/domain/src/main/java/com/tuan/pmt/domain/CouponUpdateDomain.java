package com.tuan.pmt.domain;

import java.util.List;
import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.dao.data.CpnCouponLogDO;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.repository.CouponUpdateDomainRepository;
import com.tuan.pmt.domain.support.util.DateUtils;
import com.tuan.pmt.domain.support.util.ModelConverter;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.res.CouponUpdateResEnum;
import com.tuan.pmt.model.constant.status.CouponActionCodeEnum;
import com.tuan.pmt.model.constant.status.CouponBatchStatusEnum;
import com.tuan.pmt.model.constant.status.CouponStatusEnum;

/**
 * 代金券更新操作类
 * @author duandj
 *
 */
public class CouponUpdateDomain {
	CpnCouponBatchCityDO cpnCouponBatchCityDO;
	CpnCouponBatchCatDO	CpnCouponBatchCatDO;
	CpnCouponDO cpnCouponDO;
	CpnCouponBatchDO cpnCouponBatchDO;
	List<CpnCouponDO> cpnCouponDOList;
	CouponQueryDomain couponQueryDomain;
	CouponQueryDomainRepository couponQueryDomainRepository;
	private SpyMemcachedClient youhuiSpyMemcachedClient;
	private CouponUpdateDomainRepository couponUpdateDomainRepository;
	
	/**
	 * 绑定代金券到用户
	 * @param userId	用户ID
	 * @return	boolean true:成功，false:失败
	 */
	public boolean bindCoupon2User(final long userId){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setUserId(userId);
		cpnCouponDO.setStatus(CouponStatusEnum.UNUSED.getCode());
		cpnCouponDO.setBindTime(DateUtils.getNowTimestamp());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "绑定代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " 状态改为 " + CouponStatusEnum.UNUSED.getName() + ",用户ID:" + userId;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.BIND,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
     * 校验批次
     * @param platType 		适用范围：手机端、网站端
     * @param couponCount 	绑定次数限制次数
     * @return	CouponUpdateResEnum 结果枚举
     */
    public CouponUpdateResEnum validateCouponBatch(String platType,int couponCount) {
        //1.判断cpn_coupon_batch  中status 状态限制  已保存(0)、生成中(1)、已生成(2)、已作废(3)',
        if( CouponBatchStatusEnum.CREATED.getCode() != cpnCouponBatchDO.getStatus())
            return CouponUpdateResEnum.INVALID_STATUS ;
        
        //2.判断cpn_coupon_batch  中 start_time 小于 当前时间    end_time  大于 当前时间
        if(DateUtils.checkTimeScope(DateUtils.getNowTimestamp(),cpnCouponBatchDO.getEndTime()) != 0){
        	return  CouponUpdateResEnum.INVALID_TIME ;
        }
        
        //3.判断 cpn_coupon_batch use_terminal 适用范围：手机端、网站端   判断 该批次是否应用于平台
        if(cpnCouponBatchDO.getUseTerminal() != null && cpnCouponBatchDO.getUseTerminal().indexOf(platType) < 0)
            return  CouponUpdateResEnum.INVALID_PLATTYPE ;
        
        //4.判断 cpn_coupon_batch bind_limit 绑定次数限制，判断用户绑定该批次的数量是否超过该值
        if(cpnCouponBatchDO.getBindLimit() != null && cpnCouponBatchDO.getBindLimit() > 0){
            if(couponCount >= cpnCouponBatchDO.getBindLimit()){
                return  CouponUpdateResEnum.INVALID_BATCHLIMIT ;
            }
        }
        //5.判断 cpn_coupon_batch  first_time 限制首次购买可绑定  如果设置为1,则调用订单中心判断用户是否有订单
        //6.判断 cpn_coupon_batch  bind_phone   如果设置为1, 则调用用户中心是否绑定手机
        return null;
    }
	
	/**
	 * 冻结代金券到订单
	 * @param orderId	订单ID
	 * @param couponUpdateDomain
	 * @return	boolean true:成功，false:失败
	 */
	public boolean couponFreeze(final long orderId ,CouponUpdateDomain couponUpdateDomain){
		CpnCouponDO cpnCouponDO = couponUpdateDomain.getCpnCouponDO();
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setOrderId(orderId);
		cpnCouponDO.setUsedTime(DateUtils.getNowTimestamp());
		cpnCouponDO.setStatus(CouponStatusEnum.FREEZED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "冻结代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " 状态改为 " + CouponStatusEnum.FREEZED.getName() + ",订单ID:" + orderId;
			CpnCouponLogDO cpnCouponLogDO = couponUpdateDomain.fillCpnCouponLog(CouponActionCodeEnum.FREEZE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 解冻代金券
	 * @return boolean ,true:成功; false:失败
	 */
	public boolean couponUnFreeze(){
		Integer oldStatus = cpnCouponDO.getStatus();
		long orderId = cpnCouponDO.getOrderId();
		cpnCouponDO.setOrderId(null);
		cpnCouponDO.setUsedTime(null);
		cpnCouponDO.setStatus(CouponStatusEnum.UNUSED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			cpnCouponDO.setOrderId(orderId);
			String description = "解冻代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
					"状态改为:" + CouponStatusEnum.UNUSED.getName();
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.UNFREEZE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 使用代金券
	 * @return	true:成功; false:失败
	 */
	public boolean couponUsed(){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setStatus(CouponStatusEnum.USED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "使用代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " 状态改为 " + CouponStatusEnum.USED.getName() ;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.USE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 作废代金券,根据订单ID
	 * @param	invalidAdminId	操作人ID
	 * @param	invalidReason	作废理由
	 * @return	true:成功; false:失败
	 */
	public boolean couponCancelByOrderId(final long invalidAdminId, final String invalidReason){
		for(CpnCouponDO cpnCouponDO : cpnCouponDOList){
			this.cpnCouponDO = cpnCouponDO;
			Integer oldStatus = cpnCouponDO.getStatus();
			cpnCouponDO.setStatus(CouponStatusEnum.CANCEL.getCode());
			cpnCouponDO.setInvalidTime(DateUtils.getNowTimestamp());
			cpnCouponDO.setInvalidAdminId(invalidAdminId);
			cpnCouponDO.setInvalidReason(invalidReason);
			if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
				String description = "使用代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
									 " 状态改为 " + CouponStatusEnum.CANCEL.getName() ;
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.CANCEL,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 代金券退款,根据订单ID
	 * @param	invalidAdminId	操作人ID
	 * @param	invalidReason	作废理由
	 * @return	true:成功; false:失败
	 */
	public boolean couponRefundByOrderId(final long invalidAdminId){
		for(CpnCouponDO cpnCouponDO : cpnCouponDOList){
			this.cpnCouponDO = cpnCouponDO;
			Integer oldStatus = cpnCouponDO.getStatus();
			cpnCouponDO.setStatus(CouponStatusEnum.CANCEL.getCode());
			cpnCouponDO.setInvalidTime(DateUtils.getNowTimestamp());
			cpnCouponDO.setInvalidAdminId(invalidAdminId);
			cpnCouponDO.setInvalidReason("代金券退款");
			if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
				String description = "使用代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
									 " 状态改为 " + CouponStatusEnum.CANCEL.getName() ;
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.REFUNDMENT,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 作废代金券
	 * @param	invalidAdminId	操作人ID
	 * @param	invalidReason	作废理由
	 * @return	true:成功; false:失败
	 */
	public boolean couponCancel(final long invalidAdminId, final String invalidReason){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setStatus(CouponStatusEnum.CANCEL.getCode());
		cpnCouponDO.setInvalidTime(DateUtils.getNowTimestamp());
		cpnCouponDO.setInvalidAdminId(invalidAdminId);
		cpnCouponDO.setInvalidReason(invalidReason);
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "使用代金券操作：将 " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " 状态改为 " + CouponStatusEnum.CANCEL.getName() ;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.CANCEL,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 作废批次
	 * @param invalidAdminId	作废操作员ID
	 * @param invalidReason		作废理由
	 * @return	true:成功; false:失败
	 */
	public boolean batchCancel(final long invalidAdminId, final String invalidReason){
		cpnCouponBatchDO.setStatus(CouponBatchStatusEnum.CANCEL.getCode());
		cpnCouponBatchDO.setInvalidTime(DateUtils.getNowTimestamp());
		cpnCouponBatchDO.setInvalidAdminId(invalidAdminId);
		cpnCouponBatchDO.setInvalidReason(invalidReason);
		if(couponUpdateDomainRepository.updateBatch(cpnCouponBatchDO) > 0 ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获取代金券模型对象，包括代金券，批次，城市，分类信息。
	 * @return	代金券模型对象
	 */
	public CpnCouponModel makeCouponModel(){
    	CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCityDO> cityIdList = couponQueryDomain.queryBatchCityListByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCatDO> catIdList = couponQueryDomain.queryBatchCatListByBatchId(cpnCouponDO.getBatchId());
        return ModelConverter.toCouponModel(cpnCouponDO, cpnCouponBatchDO,catIdList, cityIdList);
	}
	
	/**
	 * 解冻代金券,通过订单ID
	 * @return	true:成功; false:失败
	 */
	public boolean couponUnFreezeByOrderId(){
		for(CpnCouponDO cpnCouponDO : cpnCouponDOList){
			Integer oldStatus = cpnCouponDO.getStatus();
			long orderId = cpnCouponDO.getOrderId();
			this.cpnCouponDO = cpnCouponDO;
			cpnCouponDO.setOrderId(null);
			cpnCouponDO.setUsedTime(null);
			cpnCouponDO.setStatus(CouponStatusEnum.UNUSED.getCode());
			if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
				cpnCouponDO.setOrderId(orderId);
				String description = "解冻代金券操作：将" + CouponStatusEnum.valueOfEnum(oldStatus).getName() +
						"状态改为" + CouponStatusEnum.UNUSED.getName();
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.UNFREEZE,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 填充代金券日志对象
	 * @param couponActionCodeEnum	操作类型
	 * @param description	表述
	 * 
	 * @return	日志对象
	 */
	public CpnCouponLogDO fillCpnCouponLog(CouponActionCodeEnum couponActionCodeEnum,String description){
		if(couponActionCodeEnum == null ){
			return null;
		}
		CpnCouponLogDO cpnCouponLogDO = new CpnCouponLogDO();
		cpnCouponLogDO.setCode(couponActionCodeEnum.getCode());
		cpnCouponLogDO.setCouponId(cpnCouponDO.getCouponId());
		cpnCouponLogDO.setCreateTime(DateUtils.getNowTimestamp());
		cpnCouponLogDO.setDescription(description);
		if(	couponActionCodeEnum.compareTo(CouponActionCodeEnum.BIND) == 0){
			cpnCouponLogDO.setObjectId(cpnCouponDO.getUserId());
		}else{
			cpnCouponLogDO.setObjectId(cpnCouponDO.getOrderId());
		}
		return cpnCouponLogDO;
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
	public CouponUpdateDomainRepository getCouponUpdateDomainRepository() {
		return couponUpdateDomainRepository;
	}
	public void setCouponUpdateDomainRepository(CouponUpdateDomainRepository couponUpdateDomainRepository) {
		this.couponUpdateDomainRepository = couponUpdateDomainRepository;
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
	public List<CpnCouponDO> getCpnCouponDOList() {
		return cpnCouponDOList;
	}
	public void setCpnCouponDOList(List<CpnCouponDO> cpnCouponDOList) {
		this.cpnCouponDOList = cpnCouponDOList;
	}
	public CouponQueryDomain getCouponQueryDomain() {
		return couponQueryDomain;
	}
	public void setCouponQueryDomain(CouponQueryDomain couponQueryDomain) {
		this.couponQueryDomain = couponQueryDomain;
	}
}
