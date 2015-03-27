package com.gome.service;

import com.gome.core.common.service.GomeCallbackResult;



public interface BusiServiceTemplate {
	
	/**
	 * 模板执行
	 * @param action
	 * @return
	 */
	GomeCallbackResult execute(QuickstartServiceCallback action);

	GomeCallbackResult executeMaster(final QuickstartServiceCallback action);
}
