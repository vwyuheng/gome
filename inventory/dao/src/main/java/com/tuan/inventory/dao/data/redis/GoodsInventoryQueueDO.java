package com.tuan.inventory.dao.data.redis;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
/**
 * 库存队列结构bean
 * @author henry.yu
 * @date 20140311
 */
public class GoodsInventoryQueueDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //队列主键
	private Long goodsId;// 商品ID(FK)
	private Long orderId;// 订单id
	private Long userId ;//用户id
	private Long goodsBaseId;
	private int limitStorage; // 0:库存无限制；1：限制库存
	//需扣减的商品库存量
	private int deductNum  = 0;
	//原库存
	private int originalGoodsInventory = 0;
	//选型和分店原始库存和扣减库存的list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	
	
	private Long createTime;  //创建时间，精确到秒
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	public int getDeductNum() {
		return deductNum;
	}
	public void setDeductNum(int deductNum) {
		this.deductNum = deductNum;
	}
	public int getOriginalGoodsInventory() {
		return originalGoodsInventory;
	}
	public void setOriginalGoodsInventory(int originalGoodsInventory) {
		this.originalGoodsInventory = originalGoodsInventory;
	}
	public List<GoodsSelectionAndSuppliersResult> getSelectionParam() {
		return selectionParam;
	}
	public void setSelectionParam(
			List<GoodsSelectionAndSuppliersResult> selectionParam) {
		this.selectionParam = selectionParam;
	}
	public List<GoodsSelectionAndSuppliersResult> getSuppliersParam() {
		return suppliersParam;
	}
	public void setSuppliersParam(
			List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		this.suppliersParam = suppliersParam;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}

	
}
