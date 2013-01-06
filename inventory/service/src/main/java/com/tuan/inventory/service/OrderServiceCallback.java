package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

/**
 * ��������ص��ӿ�
 * @author ������
 * @date 2012.10.25
 */
public interface OrderServiceCallback {
    /**
     * �������
     * @return
     */
    public TuanCallbackResult executeParamsCheck(); 
    
    /**
     * ҵ����
     * @return
     */
    public TuanCallbackResult executeBusiCheck();
    
    /**
     * ҵ��ִ��
     * @return
     */
    public TuanCallbackResult executeAction();
    
    /**
     * ִ�к���
     * @return
     */
    public void executeAfter(); 
}
