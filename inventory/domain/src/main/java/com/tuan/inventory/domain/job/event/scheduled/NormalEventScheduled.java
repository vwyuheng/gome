package com.tuan.inventory.domain.job.event.scheduled;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.RedisInventoryModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * �����̣߳���Ҫ����ȷ�ϻص�ok����µĿ����Ϣ�����¼�����
 * 
 * @author henry.yu
 * @Date 2014/04/01
 */
public class NormalEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(NormalEventScheduled.class);
	
	// Ĭ�ϳ�ʼ����ʱ ��λ������
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// Ĭ�����ο�ʼִ����С���ʱ�� ��λ������
	private static final long DEFAULTDELAY = 4 * 1000;
	// ֧������
	private long initialDelay = 0;
	private long delay = 0;

	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	@Resource
	InventoryQueueService inventoryQueueService;
	@Resource
	NotifyServerSendMessage notifyServerSendMessage;
	
	/**
	 * �������������Ŀͻ���
	 */
	public NormalEventScheduled() {
		super();
	}

	/**
	 * ������һ����Ƶ��ִ����־�������¼��ķ�װ
	 */
	public void execFixedRate4Normal() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
						System.out.println("execFixedRate4Normal1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new NormalQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
						System.out.println("execFixedRate4Normal2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("NormalEventScheduled scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("NormalEventScheduled scheduled :"
										+ result.toString());
							}
							
						}
					} catch (InterruptedException e) {
						logger.error(
								"NormalEventScheduled scheduled Interrupted exception :",
								e);
						future.cancel(true);// �ж�ִ�д�������߳�
					} catch (ExecutionException e) {
						logger.error(
								"NormalEventScheduled scheduled Execution exception:", e);
						future.cancel(true);// �ж�ִ�д�������߳�
					}
				} catch (Throwable e) {
					logger.error("NormalEventScheduled scheduled Exception:", e);
				}
			}

		}.start();
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	class NormalQueueConsumeTask implements Runnable {

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("NormalQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// ˢ����һ�λ�Ծʱ��
			lastStartTime = startTime;
			List<RedisInventoryQueueDO> queueList = null;
			try {
				//ȡ��ʼ״̬������Ϣ
				queueList = inventoryProviderReadService
						.getInventoryQueueByScoreStatus(Double
								.valueOf(ResultStatusEnum.ACTIVE.getCode()));
				System.out.println("NormalQueueConsumeTask:run2="+queueList);
			} catch (Exception e) {
				logger.error("NormalQueueConsumeTask.run error", e);
			}
			//System.out.println("queueList1="+queueList.size());
			// ��������
			if (!CollectionUtils.isEmpty(queueList)) {
				logJSON.put("count", queueList.size());
				AtomicInteger realCount = new AtomicInteger();
				System.out.println("queueList2="+queueList.size());
				for (RedisInventoryQueueDO model : queueList) {
					System.out.println("model="+model.getId());
						try {
									//����keyȡ��Ϣʵ��
									RedisInventoryModel result = null;
									result = inventoryProviderReadService.getInventoryInfosByKey(String.valueOf(model.getGoodsId()));
									System.out.println("result="+result);
									if(result!=null) {
										//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
										notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(ObjectUtil.asemblyNotifyMessage(model.getUserId(),result)));
										System.out.println("notifyServerSendMessage,sended");
									}
									//��������״������״̬�ɻ�Ծ״̬��ACTIVE		("1",	"��������Ч�ɴ���active��")����Ϊɾ��:7
									inventoryQueueService.markQueueStatus(String.valueOf(model.getId()), (6));
									realCount.incrementAndGet();
						} catch (Exception e) {
							logger.error(e);
							e.printStackTrace();
						}

				}
				logJSON.put("realcount", realCount.get());
			
			}
			long endTime = System.currentTimeMillis();
			logJSON.put("costTime", endTime - startTime);
			if (logger.isDebugEnabled()) {
				logger.debug(logJSON.toString());
			}
			
		}

		/** ��ȡ��һ�ε� �ʱ�� ������� */
		public long getLastActiveTime() {
			return lastStartTime;
		}
	}

}
