package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSelectionAndSuppliersParam
 * @Description: ������Ʒѡ�ͻ�ֵ��ԭʼ���Ϳۼ����Ķ���
 * @author henry.yu
 * @date 2014-4-8
 */
public class GoodsSelectionAndSuppliersModel extends TuanBaseDO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	//��ۼ�����Ʒ���
	private int goodsInventory;
	//ԭ���
	private int originalGoodsInventory;
	public int getGoodsInventory() {
		return goodsInventory;
	}
	public void setGoodsInventory(int goodsInventory) {
		this.goodsInventory = goodsInventory;
	}
	public int getOriginalGoodsInventory() {
		return originalGoodsInventory;
	}
	public void setOriginalGoodsInventory(int originalGoodsInventory) {
		this.originalGoodsInventory = originalGoodsInventory;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
}