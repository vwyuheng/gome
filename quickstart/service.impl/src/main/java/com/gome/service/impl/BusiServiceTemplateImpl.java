package com.gome.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.gome.core.common.datasource.DataSourceContextHolder;
import com.gome.core.common.datasource.msloadbalancer.MSDataSourceModel;
import com.gome.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.gome.core.common.datasource.msloadbalancer.MSDataSourcesLoadBalancerManager;
import com.gome.core.common.lang.GomeRuntimeException;
import com.gome.core.common.service.GomeCallbackResult;
import com.gome.core.common.service.GomeServiceConstants;
import com.gome.core.common.service.GomeServiceException;
import com.gome.service.BusiServiceTemplate;
import com.gome.service.QuickstartServiceCallback;

/**
 * 业务处理模板实现类
 * @author henry.yu
 * @date 2015.03.27
 */
public class BusiServiceTemplateImpl implements BusiServiceTemplate{

    private final static Log logger = LogFactory.getLog(BusiServiceTemplateImpl.class);

   
    protected TransactionTemplate transactionTemplate;

    public void setDataSourceType(String dataSourceType){
    	if(MSDataSourceType.MASTER.equals(dataSourceType)){
    		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);
    	}else{
    		DataSourceContextHolder.setDataSourceType(MSDataSourceType.SALVE_1);
    	}
    }
    public GomeCallbackResult execute(final QuickstartServiceCallback action) {

        if (logger.isDebugEnabled()) {
            logger.debug("进入模板方法开始处理");
        }
        GomeCallbackResult result = GomeCallbackResult.success();
        try {
        	setDataSourceType(MSDataSourceType.MASTER);
        	 // 设置数据源，读写分离
            result = action.executeParamsCheck();
            
            if(result.isSuccess()){
            	
            result=action.executeBusiCheck();
            
            if (result.isSuccess()) {
            	setDataSourceType(MSDataSourceType.MASTER);
                result = (GomeCallbackResult) this.transactionTemplate.execute(new TransactionCallback() {
                	public Object doInTransaction(TransactionStatus status) {

                            // 3. 回调业务逻辑
                            // 3.1 通过annotation来实现某些option类型的扩展
                            GomeCallbackResult iNresult = action.executeAction();
                            if (null == iNresult) {
                                throw new GomeServiceException(GomeServiceConstants.SERVICE_NO_RESULT);
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

        } catch (GomeServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法A点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (GomeRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法B点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
           
            if (logger.isErrorEnabled()) {
                logger.error("异常退出模板方法C点", e);
            }
            result = GomeCallbackResult.failure(GomeServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        
        }finally{
        	
            DataSourceContextHolder.clearDataSourceType();
        
        }
        return result;
    }
   
    public GomeCallbackResult executeWithoutTransaction(final QuickstartServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("进入模板方法开始处理");
        }
        GomeCallbackResult result = GomeCallbackResult.success();
        // 设置数据源，读写分离
        
        MSDataSourceModel mSDataSourceModel = MSDataSourcesLoadBalancerManager.getAliveMSDataSource();
        
        if(mSDataSourceModel==null){
        	
        	return GomeCallbackResult.failure(GomeServiceConstants.NO_ALIVE_DATASOURCE);
        }
        try {
        	DataSourceContextHolder.setDataSourceType(mSDataSourceModel.getDataSourceType());
        	
            result = action.executeParamsCheck();
        
            if (result.isSuccess()) {
              
                result = action.executeAction();
                if (null == result) {
                    throw new GomeServiceException(GomeServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. 扩展点
                templateExtensionAfterExecute(result,action);
                if (result.isFailure()) {
                    return result;
                }
                // 5. 发送业务事件
                
            }
        } catch (GomeServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法D点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (GomeRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法E点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // FIXME: 后续可以考虑把分析具体的异常类型
            // 把系统异常转换为服务异常
            if (logger.isErrorEnabled()) {
                logger.error("异常退出模板方法F点", e);
            }
            result = GomeCallbackResult.failure(GomeServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
        	
        	 DataSourceContextHolder.clearDataSourceType();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("模板执行结束");
        }
       
        return result;
    }
    public GomeCallbackResult executeMaster(final QuickstartServiceCallback action) {

        if (logger.isDebugEnabled()) {
            logger.debug("进入模板方法开始处理");
        }
        GomeCallbackResult result = GomeCallbackResult.success();
        try {
        	setDataSourceType(MSDataSourceType.MASTER);
        	
            result = action.executeParamsCheck();
        
            if (result.isSuccess()) {
              
                result = action.executeAction();
                if (null == result) {
                    throw new GomeServiceException(GomeServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. 扩展点
                templateExtensionAfterExecute(result,action);
                if (result.isFailure()) {
                    return result;
                }
                // 5. 发送业务事件
                
            }
        } catch (GomeServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法D点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (GomeRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("异常退出模板方法E点", e);
            }
            result = GomeCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // FIXME: 后续可以考虑把分析具体的异常类型
            // 把系统异常转换为服务异常
            if (logger.isErrorEnabled()) {
                logger.error("异常退出模板方法F点", e);
            }
            result = GomeCallbackResult.failure(GomeServiceConstants.SERVICE_SYSTEM_FALIURE, e);
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
    protected void templateExtensionAfterTransaction(GomeCallbackResult result,final QuickstartServiceCallback action) {
        // DUMY
    	try {
			action.executeAfter();
		} catch (Exception e) {
			 if (logger.isErrorEnabled()) {
	                logger.error("Execute error after transaction !", e);
	            }
		}
    }

   
    protected void templateExtensionAfterExecute(GomeCallbackResult result,final QuickstartServiceCallback action) {
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

}
