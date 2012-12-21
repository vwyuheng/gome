package com.tuan.pmt.service;

import com.tuan.core.common.service.TuanCallbackResult;

public interface CouponServiceTemplate {
	/**
	 * Ä£°åÖ´ÐÐ
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(CouponServiceCallback action);
}
