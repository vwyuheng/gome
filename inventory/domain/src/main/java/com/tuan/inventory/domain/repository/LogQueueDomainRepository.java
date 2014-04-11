package com.tuan.inventory.domain.repository;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.domain.LogQueueDomain;
import com.tuan.inventory.domain.support.BaseQueueDomain;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.util.QueueConstant;

/**
 * ���вִ���Ϣ
 * 
 * @author henry.yu
 * @Date  2014/04/04
 */
public class LogQueueDomainRepository {
	
	private static final Log logger = LogFactory.getLog(LogQueueDomainRepository.class);
	
	/**
	 * ��־������Ϣdao
	 */
	@Resource
	private LogOfWaterDAO logOfWaterDAO;

	public LogQueueDomain createQueueDomain(GoodsInventoryActionModel logModel) {
		
		return new LogQueueDomain(logModel);
	}

	/**
	 * ������Ϣ����
	 * @param queueDomain 
	 */
	public void saveLogOfWater(BaseQueueDomain logDomain) {
		String uniqueSign = null;
		try {
			GoodsInventoryActionDO logQueueDO = logDomain.toLogQueueDO();
			logOfWaterDAO.insertInventoryQueue(logQueueDO);
		} catch (Exception e) {
			logger.error(
					"LogQueueDomainRepository.saveLogOfWater error occured!"
							+ e.getMessage(), e);
			if (e instanceof DataIntegrityViolationException) {// ��Ϣ�����ظ�
				throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
						"Duplicate entry '" + uniqueSign
								+ "' for key 'unique_sign'", e);
			}
			throw new TuanRuntimeException(
					QueueConstant.SERVICE_DATABASE_FALIURE,
					"LogQueueDomainRepository.saveLogOfWater error occured!",
					e);
		}
	}
	
	
	
}