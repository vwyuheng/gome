package com.tuan.inventory.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceModel;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourcesLoadBalancerManager;
import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceConstants;
import com.tuan.core.common.service.TuanServiceException;
import com.tuan.inventory.service.InventoryQueryServiceCallback;
import com.tuan.inventory.service.InventoryUpdateServiceCallback;
import com.tuan.inventory.service.InventoryServiceTemplate;

/**
 * 订单中心业务处理模板实现类
 * @author tianzq
 * @date 2012.10.25
 */
public class InventoryServiceTemplateImpl implements InventoryServiceTemplate{

    private final static Log logger = LogFactory.getLog(InventoryServiceTemplateImpl.class);

   
    protected TransactionTemplate transactionTemplate;

    public void setDataSourceType(String dataSourceType){
    	if(MSDataSourceType.MASTER.equals(dataSourceType)){
    		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);
    	}else{
    		DataSourceContextHolder.setDataSourceType(MSDataSourceType.SALVE_1);
    	}
    }
    public TuanCallbackResult execute(final InventoryUpdateServiceCallback action) {

        if (logger.isDebugEnabled()) {
            logger.debug("进入模板方法开始处理");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        try {
        	setDataSourceType(MSDataSourceType.MASTER);
        	 // 设置数据源，读写分离
            result = action.executeParamsCheck();
            
            if(result.isSuccess()){
            	
            result=action.executeBusiCheck();
            
            if (result.isSuccess()) {
            	setDataSourceType(MSDataSourceType.MASTER);
                result = (TuanCallbackResult) this.transactionTemplate.execute(new TransactionCallback() {
                	public Object doInTransaction(TransactionStatus status) {

                            // 3. 回调业务逻辑
                            // 3.1 通过annotation来实现某些option类型的扩展
                            TuanCallbackResult iNresult = action.executeAction();
                            if (null == iNresult) {
                                throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                            }
                            if (iNresult.isFailure()) {
                                status.setRollbackOnly();
                                return iNresult;
                            }
              
                            return iNresult;
                        }
                    });
               
                if (result.isSuccess()) {
                	setDataSourceType(MSDataSourceType.MASTER);
                	templateExtensionAfterTransaction(result,action);
                }
            
            }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("正常退出模板方法");
            }

        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法A点", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (TuanRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法B点", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
           
            if (logger.isErrorEnabled()) {
                logger.error("异常退出模板方法C点", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        
        }finally{
        	
            DataSourceContextHolder.clearDataSourceType();
        
        }
        return result;
    }
   
    public TuanCallbackResult executeWithoutTransaction(final InventoryUpdateServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("进入模板方法开始处理");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        // 设置数据源，读写分离
        
        MSDataSourceModel mSDataSourceModel = MSDataSourcesLoadBalancerManager.getAliveMSDataSource();
        
        if(mSDataSourceModel==null){
        	
        	return TuanCallbackResult.failure(TuanServiceConstants.NO_ALIVE_DATASOURCE);
        }
        try {
        	DataSourceContextHolder.setDataSourceType(mSDataSourceModel.getDataSourceType());
        	
            result = action.executeParamsCheck();
        
            if (result.isSuccess()) {
              
                result = action.executeAction();
                if (null == result) {
                    throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. 扩展点
                templateExtensionAfterExecute(result,action);
                if (result.isFailure()) {
                    return result;
                }
                // 5. 发送业务事件
                
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法D点", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (TuanRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法E点", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // FIXME: 后续可以考虑把分析具体的异常类型
            // 把系统异常转换为服务异常
            if (logger.isErrorEnabled()) {
                logger.error("异常退出模板方法F点", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
        	
        	 DataSourceContextHolder.clearDataSourceType();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("模板执行结束");
        }
       
        return result;
    }
    
    /**
     * 扩展点：模板提供的允许不同类型业务在<b>事务内</b>进行扩展的一个点
     * 
     * @param serviceContext
     * @param domain
     */
    protected void templateExtensionAfterTransaction(TuanCallbackResult result,final InventoryUpdateServiceCallback action) {
        // DUMY
    	try {
			action.executeAfter();
		} catch (Exception e) {
			 if (logger.isErrorEnabled()) {
	                logger.error("Execute error after transaction !", e);
	            }
		}
    }

   
    protected void templateExtensionAfterExecute(TuanCallbackResult result,final InventoryUpdateServiceCallback action) {
        // DUMY
    	try {
			action.executeAfter();
		} catch (Exception e) { 
			 if (logger.isErrorEnabled()) {
	                logger.error("Extension Execute error !", e);
	          }
		}
    }

    // ---------------- 注入方法 ------------------------------

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
	@Override
	public TuanCallbackResult execute(InventoryQueryServiceCallback action) {
		// TODO Auto-generated method stub
		return null;
	}

}
