package com.tuan.pmt.service;

import com.tuan.core.common.service.TuanCallbackResult;

public interface CouponServiceTemplate {
	/**
	 * ģ��ִ��
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(CouponServiceCallback action);
}
