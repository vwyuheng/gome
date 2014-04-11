package com.tuan.inventory.dao.data.redis;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
/**
 * �����нṹbean
 * @author henry.yu
 * @date 20140311
 */
public class GoodsInventoryQueueDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //��������
	private Long goodsId;// ��ƷID(FK)
	private Long orderId;// ����id
	private Long userId ;//�û�id
	//��ۼ�����Ʒ�����
	private int deductNum  = 0;
	//ԭ���
	private int originalGoodsInventory = 0;
	//ѡ�ͺͷֵ�ԭʼ���Ϳۼ�����list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	
	
	private Long createTime;  //����ʱ�䣬��ȷ����
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

	
}