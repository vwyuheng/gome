package com.tuan.inventory.model.result;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.enu.res.InventoryQueryEnum;

public class InventoryQueryResult extends TuanBaseDO {

	private static final long serialVersionUID = 549836427059615587L;
	/**用户订单列表查询结果*/
	private InventoryQueryEnum result;

	/**业务结果对象*/
	private Object resultObject;

	public InventoryQueryResult(InventoryQueryEnum result,
			Object resultObject) {
		super();
		this.result = result;
		this.resultObject = resultObject;
	}

	public InventoryQueryEnum getResult() {
		return result;
	}

	public void setResult(InventoryQueryEnum result) {
		this.result = result;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}


}
