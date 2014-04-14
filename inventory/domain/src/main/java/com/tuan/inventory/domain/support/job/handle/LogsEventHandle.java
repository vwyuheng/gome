package com.tuan.inventory.domain.support.job.handle;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.domain.LogOfWaterHandleService;
import com.tuan.inventory.domain.support.exception.CacheRunTimeException;
import com.tuan.inventory.domain.support.job.event.Event;
import com.tuan.inventory.domain.support.job.event.EventHandle;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;

/**
 * ��־�첽�����¼�
 * @author henry.yu
 * @Date  2014/3/21
 */
public class LogsEventHandle implements EventHandle {
	
	private final static LocalLogger log = LocalLogger.getLog("LogsEventHandle.LOG");
	
	@Resource
	LogOfWaterHandleService logOfWaterHandleService;
	
	@Override
	public boolean handleEvent(final Event event) throws Exception{
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(event == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("LogsEventHandle.handleEvent");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("event",event)
				.addMetaData("startTime", startTime).toJson());
		GoodsInventoryActionModel logModel = null;
		CallResult<GoodsInventoryActionModel> callResult  = null;
		try {
			logModel = (GoodsInventoryActionModel) event.getData();
			if (logModel != null) {
				// ���Ѷ��е���Ϣ
				callResult = logOfWaterHandleService.createLogOfWater(logModel);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED) {  //�������Ѿ�����ʱ����true,Ϊ����ɾ�������еĶ�������
					// ��Ϣ���ݲ��沢�Ҳ��ɹ�
					isSuccess = false;
					message = "queue_error[" + publicCodeEnum.getMessage()
							+ "]logid:" + logModel.getId();
				} else {
					message = "queue_success[save success]logid:" + logModel.getId();
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("event",event)
					.addMetaData("element",logModel)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			throw new CacheRunTimeException("LogsEventHandle.handleEvent run exception!",e);
		}
		log.info(lm.addMetaData("event",event)
				.addMetaData("element",logModel)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	


}
