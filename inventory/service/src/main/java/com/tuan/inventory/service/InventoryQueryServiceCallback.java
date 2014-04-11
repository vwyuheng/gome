package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

/**
 * ��������ص��ӿ�
 * @author ������
 * @date 2012.10.25
 */
public interface InventoryQueryServiceCallback {
    /**
     * �������
     * @return
     */
    public TuanCallbackResult preHandler(); 
    
    /**
     * ҵ��ִ��
     * @return
     */
    public TuanCallbackResult doWork();
    
    
}
