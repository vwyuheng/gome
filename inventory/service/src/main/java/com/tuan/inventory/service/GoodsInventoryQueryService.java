package com.tuan.inventory.service;

import java.util.List;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.result.CallResult;

public interface GoodsInventoryQueryService {
	/**
	 * ����ѡ��id��ȡѡ�Ϳ����Ϣ
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param selectionId
	 * @return
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(final String clientIp, final String clientName, final long goodsId,
			final long selectionId);
	/**
	 * ������Ʒ�ֵ�id��ȡ�ֵ���
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param suppliersId
	 * @return
	 */
	@ProductCode(code = "00002", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsSuppliersModel> findGoodsSuppliersBySuppliersId(final String clientIp, final String clientName, final long goodsId,
			final long suppliersId);
	/**
	 * ������Ʒid��ȡ��Ʒ�����Ϣ��
	 * ����(���еĻ�)ѡ�͵Ŀ�漰�ֵ�Ŀ��
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00003", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * ������Ʒid��ȡ��Ʒѡ�Ϳ���б�
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00004", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSelectionModel>> findGoodsSelectionListByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * ������Ʒid��ȡ��Ʒ�ֵ����б�
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00005", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
}
