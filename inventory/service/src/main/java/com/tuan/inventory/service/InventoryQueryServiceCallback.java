package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

/**
 * 订单服务回调接口
 * @author 窝窝团
 * @date 2012.10.25
 */
public interface InventoryQueryServiceCallback {
    /**
     * 参数检查
     * @return
     */
    public TuanCallbackResult preHandler(); 
    
    /**
     * 业务执行
     * @return
     */
    public TuanCallbackResult doWork();
    
    
}
