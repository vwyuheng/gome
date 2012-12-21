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
 * ����ȯ���²�����
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
	 * �󶨴���ȯ���û�
	 * @param userId	�û�ID
	 * @return	boolean true:�ɹ���false:ʧ��
	 */
	public boolean bindCoupon2User(final long userId){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setUserId(userId);
		cpnCouponDO.setStatus(CouponStatusEnum.UNUSED.getCode());
		cpnCouponDO.setBindTime(DateUtils.getNowTimestamp());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "�󶨴���ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " ״̬��Ϊ " + CouponStatusEnum.UNUSED.getName() + ",�û�ID:" + userId;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.BIND,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
     * У������
     * @param platType 		���÷�Χ���ֻ��ˡ���վ��
     * @param couponCount 	�󶨴������ƴ���
     * @return	CouponUpdateResEnum ���ö��
     */
    public CouponUpdateResEnum validateCouponBatch(String platType,int couponCount) {
        //1.�ж�cpn_coupon_batch  ��status ״̬����  �ѱ���(0)��������(1)��������(2)��������(3)',
        if( CouponBatchStatusEnum.CREATED.getCode() != cpnCouponBatchDO.getStatus())
            return CouponUpdateResEnum.INVALID_STATUS ;
        
        //2.�ж�cpn_coupon_batch  �� start_time С�� ��ǰʱ��    end_time  ���� ��ǰʱ��
        if(DateUtils.checkTimeScope(DateUtils.getNowTimestamp(),cpnCouponBatchDO.getEndTime()) != 0){
        	return  CouponUpdateResEnum.INVALID_TIME ;
        }
        
        //3.�ж� cpn_coupon_batch use_terminal ���÷�Χ���ֻ��ˡ���վ��   �ж� �������Ƿ�Ӧ����ƽ̨
        if(cpnCouponBatchDO.getUseTerminal() != null && cpnCouponBatchDO.getUseTerminal().indexOf(platType) < 0)
            return  CouponUpdateResEnum.INVALID_PLATTYPE ;
        
        //4.�ж� cpn_coupon_batch bind_limit �󶨴������ƣ��ж��û��󶨸����ε������Ƿ񳬹���ֵ
        if(cpnCouponBatchDO.getBindLimit() != null && cpnCouponBatchDO.getBindLimit() > 0){
            if(couponCount >= cpnCouponBatchDO.getBindLimit()){
                return  CouponUpdateResEnum.INVALID_BATCHLIMIT ;
            }
        }
        //5.�ж� cpn_coupon_batch  first_time �����״ι���ɰ�  �������Ϊ1,����ö��������ж��û��Ƿ��ж���
        //6.�ж� cpn_coupon_batch  bind_phone   �������Ϊ1, ������û������Ƿ���ֻ�
        return null;
    }
	
	/**
	 * �������ȯ������
	 * @param orderId	����ID
	 * @param couponUpdateDomain
	 * @return	boolean true:�ɹ���false:ʧ��
	 */
	public boolean couponFreeze(final long orderId ,CouponUpdateDomain couponUpdateDomain){
		CpnCouponDO cpnCouponDO = couponUpdateDomain.getCpnCouponDO();
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setOrderId(orderId);
		cpnCouponDO.setUsedTime(DateUtils.getNowTimestamp());
		cpnCouponDO.setStatus(CouponStatusEnum.FREEZED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "�������ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " ״̬��Ϊ " + CouponStatusEnum.FREEZED.getName() + ",����ID:" + orderId;
			CpnCouponLogDO cpnCouponLogDO = couponUpdateDomain.fillCpnCouponLog(CouponActionCodeEnum.FREEZE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * �ⶳ����ȯ
	 * @return boolean ,true:�ɹ�; false:ʧ��
	 */
	public boolean couponUnFreeze(){
		Integer oldStatus = cpnCouponDO.getStatus();
		long orderId = cpnCouponDO.getOrderId();
		cpnCouponDO.setOrderId(null);
		cpnCouponDO.setUsedTime(null);
		cpnCouponDO.setStatus(CouponStatusEnum.UNUSED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			cpnCouponDO.setOrderId(orderId);
			String description = "�ⶳ����ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
					"״̬��Ϊ:" + CouponStatusEnum.UNUSED.getName();
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.UNFREEZE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ʹ�ô���ȯ
	 * @return	true:�ɹ�; false:ʧ��
	 */
	public boolean couponUsed(){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setStatus(CouponStatusEnum.USED.getCode());
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "ʹ�ô���ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " ״̬��Ϊ " + CouponStatusEnum.USED.getName() ;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.USE,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ���ϴ���ȯ,���ݶ���ID
	 * @param	invalidAdminId	������ID
	 * @param	invalidReason	��������
	 * @return	true:�ɹ�; false:ʧ��
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
				String description = "ʹ�ô���ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
									 " ״̬��Ϊ " + CouponStatusEnum.CANCEL.getName() ;
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.CANCEL,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ����ȯ�˿�,���ݶ���ID
	 * @param	invalidAdminId	������ID
	 * @param	invalidReason	��������
	 * @return	true:�ɹ�; false:ʧ��
	 */
	public boolean couponRefundByOrderId(final long invalidAdminId){
		for(CpnCouponDO cpnCouponDO : cpnCouponDOList){
			this.cpnCouponDO = cpnCouponDO;
			Integer oldStatus = cpnCouponDO.getStatus();
			cpnCouponDO.setStatus(CouponStatusEnum.CANCEL.getCode());
			cpnCouponDO.setInvalidTime(DateUtils.getNowTimestamp());
			cpnCouponDO.setInvalidAdminId(invalidAdminId);
			cpnCouponDO.setInvalidReason("����ȯ�˿�");
			if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
				String description = "ʹ�ô���ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
									 " ״̬��Ϊ " + CouponStatusEnum.CANCEL.getName() ;
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.REFUNDMENT,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ���ϴ���ȯ
	 * @param	invalidAdminId	������ID
	 * @param	invalidReason	��������
	 * @return	true:�ɹ�; false:ʧ��
	 */
	public boolean couponCancel(final long invalidAdminId, final String invalidReason){
		Integer oldStatus = cpnCouponDO.getStatus();
		cpnCouponDO.setStatus(CouponStatusEnum.CANCEL.getCode());
		cpnCouponDO.setInvalidTime(DateUtils.getNowTimestamp());
		cpnCouponDO.setInvalidAdminId(invalidAdminId);
		cpnCouponDO.setInvalidReason(invalidReason);
		if(couponUpdateDomainRepository.updateCoupon(cpnCouponDO) > 0 ){
			String description = "ʹ�ô���ȯ�������� " + CouponStatusEnum.valueOfEnum(oldStatus).getName() + 
								 " ״̬��Ϊ " + CouponStatusEnum.CANCEL.getName() ;
			CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.CANCEL,description);
			couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ��������
	 * @param invalidAdminId	���ϲ���ԱID
	 * @param invalidReason		��������
	 * @return	true:�ɹ�; false:ʧ��
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
	 * ��ȡ����ȯģ�Ͷ��󣬰�������ȯ�����Σ����У�������Ϣ��
	 * @return	����ȯģ�Ͷ���
	 */
	public CpnCouponModel makeCouponModel(){
    	CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCityDO> cityIdList = couponQueryDomain.queryBatchCityListByBatchId(cpnCouponDO.getBatchId());
    	List<CpnCouponBatchCatDO> catIdList = couponQueryDomain.queryBatchCatListByBatchId(cpnCouponDO.getBatchId());
        return ModelConverter.toCouponModel(cpnCouponDO, cpnCouponBatchDO,catIdList, cityIdList);
	}
	
	/**
	 * �ⶳ����ȯ,ͨ������ID
	 * @return	true:�ɹ�; false:ʧ��
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
				String description = "�ⶳ����ȯ��������" + CouponStatusEnum.valueOfEnum(oldStatus).getName() +
						"״̬��Ϊ" + CouponStatusEnum.UNUSED.getName();
				CpnCouponLogDO cpnCouponLogDO = fillCpnCouponLog(CouponActionCodeEnum.UNFREEZE,description);
				couponUpdateDomainRepository.insertCouponLog(cpnCouponLogDO);
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ������ȯ��־����
	 * @param couponActionCodeEnum	��������
	 * @param description	����
	 * 
	 * @return	��־����
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
