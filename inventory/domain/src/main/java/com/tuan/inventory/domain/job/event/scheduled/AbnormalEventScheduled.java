package com.tuan.inventory.domain.job.event.scheduled;

import java.net.MalformedURLException;
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
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.RedisInventoryModel;
import com.tuan.inventory.model.enu.ClientNameEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.util.DateUtils;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

/**
 * �����̣߳���Ҫ���ڷ���������µĿ����Ϣ�����¼�����
 * 
 * @author henry.yu
 * @Date 2014/3/24
 */
public class AbnormalEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(AbnormalEventScheduled.class);
	
	// Ĭ�ϳ�ʼ����ʱ ��λ������
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// Ĭ�����ο�ʼִ����С���ʱ�� ��λ������
	private static final long DEFAULTDELAY = 4 * 1000;
	//Ĭ�ϼ��ʱ��,�뵱ǰʱ�����  ��λ:����
	private static final int DEFAULTPERIOD = 5;
	// ֧������
	private long initialDelay = 0;
	private long delay = 0;
	private int period = 0;

	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	@Resource
	private InventoryDeductWriteService inventoryDeductWriteService;
	@Resource
	InventoryQueueService inventoryQueueService;
	@Resource
	NotifyServerSendMessage notifyServerSendMessage;
	
	/**
	 * �������������Ŀͻ���
	 */
	public AbnormalEventScheduled() {
		super();
	}

	/**
	 * ������һ����Ƶ��ִ����־�������¼��ķ�װ
	 */
	public void execFixedRate4Abnormal() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
//						System.out.println("execFixedRate4Abnormal1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new AbnormalQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
//						System.out.println("execFixedRate4Abnormal2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("AbnormalEventScheduled scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("AbnormalEventScheduled scheduled :"
										+ result.toString());
							}
							
						}
					} catch (InterruptedException e) {
						logger.error(
								"AbnormalEventScheduled scheduled Interrupted exception :",
								e);
						future.cancel(true);// �ж�ִ�д�������߳�
					} catch (ExecutionException e) {
						logger.error(
								"AbnormalEventScheduled scheduled Execution exception:", e);
						future.cancel(true);// �ж�ִ�д�������߳�
					}
				} catch (Throwable e) {
					logger.error("AbnormalEventScheduled scheduled Exception:", e);
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

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	class AbnormalQueueConsumeTask implements Runnable {

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("AbnormalQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// ˢ����һ�λ�Ծʱ��
			lastStartTime = startTime;
			List<RedisInventoryQueueDO> queueList = null;
			try {
				//ȡ��ʼ״̬������Ϣ
				queueList = inventoryProviderReadService
						.getInventoryQueueByScoreStatus(Double
								.valueOf(ResultStatusEnum.LOCKED.getCode()));
//				System.out.println("AbnormalQueueConsumeTask:run2="+queueList);
			} catch (Exception e) {
				logger.error("AbnormalQueueConsumeTask.run error", e);
			}
//			System.out.println("queueList1="+queueList.size());
			// ��������
			if (!CollectionUtils.isEmpty(queueList)) {
				logJSON.put("count", queueList.size());
				AtomicInteger realCount = new AtomicInteger();
//				System.out.println("queueList2="+queueList.size());
				for (RedisInventoryQueueDO model : queueList) {
//					System.out.println("model="+model.getId());
//					System.out.println("����ʱ��="+TimeUtil.dateFormat(model.getCreateTime()));
//					System.out.println("��ǰʱ��="+TimeUtil.dateFormat(TimeUtil.getNowTimestamp10Long()));
//					System.out.println("5����ǰʱ��="+TimeUtil.dateFormat(DateUtils.getBeforXTimestamp10Long(getPeriod()==0?DEFAULTPERIOD:getPeriod())));
//					
//					System.out.println("�ȽϽ��="+(model.getCreateTime()<=DateUtils.getBeforXTimestamp10Long(getPeriod()==0?DEFAULTPERIOD:getPeriod())));
					//�жϸö��д���ʱ���뵱ǰʱ����ȣ��Ƿ����Period���ӻ�δ������(����״̬Ϊ��ACTIVE 1 ��������Ч�ɴ���),��ʱ������Ϊ�쳣���д���
					if(model.getCreateTime()<=DateUtils.getBeforXTimestamp10Long(getPeriod()==0?DEFAULTPERIOD:getPeriod())) {
						try {
							//��hessian����ȡ����֧��״̬
							OrderQueryService basic = (OrderQueryService) HessianProxyUtil
									.getObject(OrderQueryService.class,
											InventoryConfig.QUERY_URL);
							CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(model.getOrderId()));
							OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
//							System.out.println("statEnum="+statEnum);
							if(statEnum!=null)
							{
								try {
									//1.������״̬Ϊ�Ѹ���ʱ
									if (statEnum
											.equals(OrderInfoPayStatusEnum.PAIED)) {
										//TODO 1 ����notifyserver��Ϣ֪ͨ 2.������״̬���ɾ��
										//����keyȡ��Ϣʵ��
										RedisInventoryModel result = null;
										result = inventoryProviderReadService
												.getInventoryInfosByKey(String
														.valueOf(model
																.getGoodsId()));
										//System.out.println("result="+result);
										if (result != null) {
											//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
											notifyServerSendMessage
													.sendNotifyServerMessage(JSONObject
															.fromObject(ObjectUtil
																	.asemblyNotifyMessage(
																			model.getUserId(),
																			result)));
											//System.out.println("notifyServerSendMessage,sended");
										}
									} else { //��ȻΪ��ʼ״̬ʱ:3
										//TODO ��ԭ���ۼ��Ŀ��
										// �ع����
										inventoryQueueService
												.rollbackInventoryCache(
														String.valueOf(model
																.getId()), (4));

										//System.out.println("rollbackInventoryCache,rollbacked");

									}
									//���÷�����״������״̬�ɳ�ʼ״̬��������3����Ϊɾ��:7
									inventoryQueueService.markQueueStatus(
											String.valueOf(model.getId()), (4));
								} catch (Exception e) {
									logger.error("AbnormalQueueConsumeTask.run error", e);
								}
							}
						} catch (MalformedURLException e) {
							logger.error(e);
							e.printStackTrace();
						}
						
					}
					realCount.incrementAndGet();
					
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
