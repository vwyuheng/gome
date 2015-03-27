package com.gome.service;

import com.gome.core.common.service.GomeCallbackResult;

/**
 * 服务回调接口
 * @author henry.yu
 * @date 2015.03.27
 */
public interface QuickstartServiceCallback {
    /**
     * 参数检查
     * @return
     */
    public GomeCallbackResult executeParamsCheck(); 
    
    /**
     * 业务检查
     * @return
     */
    public GomeCallbackResult executeBusiCheck();
    
    /**
     * 业务执行
     * @return
     */
    public GomeCallbackResult executeAction();
    
    /**
     * 执行后处理
     * @return
     */
    public void executeAfter(); 
}
