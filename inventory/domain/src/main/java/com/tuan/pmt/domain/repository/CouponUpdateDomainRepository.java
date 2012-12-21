package com.tuan.pmt.domain.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.pmt.dao.CpnCouponBatchDAO;
import com.tuan.pmt.dao.CpnCouponDAO;
import com.tuan.pmt.dao.CpnCouponLogDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.dao.data.CpnCouponLogDO;

public class CouponUpdateDomainRepository {
	private CpnCouponDAO cpnCouponDAO ;
	private CpnCouponBatchDAO cpnCouponBatchDAO ;
	private CpnCouponLogDAO cpnCouponLogDAO;
	protected static Log logBus = LogFactory.getLog("BUSINESS.USER");
	
	public Integer updateCoupon(CpnCouponDO cpnCouponDO){
		return cpnCouponDAO.update(cpnCouponDO);
	}
	
	public Long insertCoupon(CpnCouponLogDO cpnCouponLogDO){
		return cpnCouponLogDAO.insert(cpnCouponLogDO);
	}

	public Long insertCouponLog(CpnCouponLogDO cpnCouponLogDO){
		return cpnCouponLogDAO.insert(cpnCouponLogDO);
	}
	
	public Integer updateBatch(CpnCouponBatchDO cpnCouponBatchDO){
		return cpnCouponBatchDAO.update(cpnCouponBatchDO);
	}
	
	public CpnCouponDAO getCpnCouponDAO() {
		return cpnCouponDAO;
	}
	public void setCpnCouponDAO(CpnCouponDAO cpnCouponDAO) {
		this.cpnCouponDAO = cpnCouponDAO;
	}
	public CpnCouponLogDAO getCpnCouponLogDAO() {
		return cpnCouponLogDAO;
	}
	public void setCpnCouponLogDAO(CpnCouponLogDAO cpnCouponLogDAO) {
		this.cpnCouponLogDAO = cpnCouponLogDAO;
	}
	public CpnCouponBatchDAO getCpnCouponBatchDAO() {
		return cpnCouponBatchDAO;
	}
	public void setCpnCouponBatchDAO(CpnCouponBatchDAO cpnCouponBatchDAO) {
		this.cpnCouponBatchDAO = cpnCouponBatchDAO;
	}
}
