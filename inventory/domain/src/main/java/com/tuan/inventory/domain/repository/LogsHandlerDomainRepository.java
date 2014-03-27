package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.AbstractDomainRepository;

public class LogsHandlerDomainRepository extends AbstractDomainRepository {
	private LogOfWaterDAO logOfWaterDAO;
	
	public void insertLogsRecord(RedisInventoryLogDO logDO){
		logOfWaterDAO.insertInventoryQueue(logDO);
	}
	public void setLogOfWaterDAO(LogOfWaterDAO logOfWaterDAO) {
		this.logOfWaterDAO = logOfWaterDAO;
	}
	
	

}
