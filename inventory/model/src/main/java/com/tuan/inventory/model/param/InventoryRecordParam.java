package com.tuan.inventory.model.param;

import java.util.LinkedHashMap;


public class InventoryRecordParam {
private String op_type;
private String actions; //接口类型：createstock  'addsales','upwmsdata','oradjusti',
private String tokenid;//redis序列ID
private String goods_id;//商品ID,
private LinkedHashMap<String, String> data;//数据数组(原传送数据结构))
public String getOp_type() {
	return op_type;
}
public void setOp_type(String op_type) {
	this.op_type = op_type;
}
public String getActions() {
	return actions;
}
public void setActions(String actions) {
	this.actions = actions;
}
public String getTokenid() {
	return tokenid;
}
public void setTokenid(String tokenid) {
	this.tokenid = tokenid;
}
public String getGoods_id() {
	return goods_id;
}
public void setGoods_id(String goods_id) {
	this.goods_id = goods_id;
}
public LinkedHashMap<String, String> getData() {
	return data;
}
public void setData(LinkedHashMap<String, String> data) {
	this.data = data;
}

}
