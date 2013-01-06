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
import com.tuan.inventory.service.BusiServiceTemplate;
import com.tuan.inventory.service.OrderServiceCallback;

/**
 * ��������ҵ����ģ��ʵ����
 * @author tianzq
 * @date 2012.10.25
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
    public TuanCallbackResult execute(final OrderServiceCallback action) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        try {
        	setDataSourceType(MSDataSourceType.MASTER);
        	 // ��������Դ����д����
            result = action.executeParamsCheck();
            
            if(result.isSuccess()){
            	
            result=action.executeBusiCheck();
            
            if (result.isSuccess()) {
            	setDataSourceType(MSDataSourceType.MASTER);
                result = (TuanCallbackResult) this.transactionTemplate.execute(new TransactionCallback() {
                	public Object doInTransaction(TransactionStatus status) {

                            // 3. �ص�ҵ���߼�
                            // 3.1 ͨ��annotation��ʵ��ĳЩoption���͵���չ
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
                logger.debug("�����˳�ģ�巽��");
            }

        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��A��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (TuanRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��B��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
           
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��C��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        
        }finally{
        	
            DataSourceContextHolder.clearDataSourceType();
        
        }
        return result;
    }
   
    public TuanCallbackResult executeWithoutTransaction(final OrderServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        // ��������Դ����д����
        
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

                // 4. ��չ��
                templateExtensionAfterExecute(result,action);
                if (result.isFailure()) {
                    return result;
                }
                // 5. ����ҵ���¼�
                
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��D��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (TuanRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��E��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // FIXME: �������Կ��ǰѷ���������쳣����
            // ��ϵͳ�쳣ת��Ϊ�����쳣
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��F��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
        	
        	 DataSourceContextHolder.clearDataSourceType();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ģ��ִ�н���");
        }
       
        return result;
    }
    public TuanCallbackResult executeMaster(final OrderServiceCallback action) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        try {
        	setDataSourceType(MSDataSourceType.MASTER);
        	
            result = action.executeParamsCheck();
        
            if (result.isSuccess()) {
              
                result = action.executeAction();
                if (null == result) {
                    throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. ��չ��
                templateExtensionAfterExecute(result,action);
                if (result.isFailure()) {
                    return result;
                }
                // 5. ����ҵ���¼�
                
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��D��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (TuanRuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��E��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // FIXME: �������Կ��ǰѷ���������쳣����
            // ��ϵͳ�쳣ת��Ϊ�����쳣
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��F��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
        	
        	 DataSourceContextHolder.clearDataSourceType();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ģ��ִ�н���");
        }
       
        return result;
    }
    /**
     * ��չ�㣺ģ���ṩ������ͬ����ҵ����<b>������</b>������չ��һ����
     * 
     * @param serviceContext
     * @param domain
     */
    protected void templateExtensionAfterTransaction(TuanCallbackResult result,final OrderServiceCallback action) {
        // DUMY
    	try {
			action.executeAfter();
		} catch (Exception e) {
			 if (logger.isErrorEnabled()) {
	                logger.error("Execute error after transaction !", e);
	            }
		}
    }

   
    protected void templateExtensionAfterExecute(TuanCallbackResult result,final OrderServiceCallback action) {
        // DUMY
    	try {
			action.executeAfter();
		} catch (Exception e) { 
			 if (logger.isErrorEnabled()) {
	                logger.error("Extension Execute error !", e);
	          }
		}
    }

    // ---------------- ע�뷽�� ------------------------------

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

}
