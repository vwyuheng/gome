package com.tuan.inventory.domain.support.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;

public class StringUtil {
    /**
     * 多个选型id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<GoodsSelectionDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//多个选型id以空格间隔的字符串
		for(GoodsSelectionDO rgsr:rgsrList) {
			sb.append(rgsr.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	
	public static String strHandler(int goodsDeductNum,int selectionDeductNum,int suppliersDeductNum){
		String inventoryChangeNum = null;
		if(goodsDeductNum!=0) {
			inventoryChangeNum = "商品总:"+String
					.valueOf(goodsDeductNum);
		}else if(selectionDeductNum!=0) {
			inventoryChangeNum = StringUtils.isEmpty(inventoryChangeNum)?"选型总:"+String
					.valueOf(selectionDeductNum):inventoryChangeNum+",选型总:"+String
					.valueOf(selectionDeductNum);
		}else if(suppliersDeductNum!=0) {
			inventoryChangeNum = StringUtils.isEmpty(inventoryChangeNum)?"分店总:"+String
					.valueOf(suppliersDeductNum):inventoryChangeNum+",分店总:"+String
					.valueOf(suppliersDeductNum);
		}
		return inventoryChangeNum;
	}
	public static String handlerOriInventory(String type,int origoodsleftnum,int origoodstotalnum,int oriselOrSuppleftnum,int oriselOrSupptotalnum){
		String result = null;
		if(!StringUtils.isEmpty(type)) {
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				result = "goodsleftnum:"+String
						.valueOf(origoodsleftnum)+",goodstotalnum:"+String
						.valueOf(origoodstotalnum);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				
				result = (origoodsleftnum!=0&&origoodstotalnum!=0)?"goodsleftnum:"+String
						.valueOf(origoodsleftnum)+",goodstotalnum:"+String
						.valueOf(origoodstotalnum):"";
						
				result = StringUtils.isEmpty(result)?"selleftnum:"+String
						.valueOf(oriselOrSuppleftnum)+",seltotalnum:"+String
						.valueOf(oriselOrSupptotalnum):result+",selleftnum:"+String
						.valueOf(oriselOrSuppleftnum)+",seltotalnum:"+String
						.valueOf(oriselOrSupptotalnum);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				result = (origoodsleftnum!=0&&origoodstotalnum!=0)?"goodsleftnum:"+String
						.valueOf(origoodsleftnum)+",goodstotalnum:"+String
						.valueOf(origoodstotalnum):"";
						
				result = StringUtils.isEmpty(result)?"suppleftnum:"+String
						.valueOf(oriselOrSuppleftnum)+",supptotalnum:"+String
						.valueOf(oriselOrSupptotalnum):result+",suppleftnum:"+String
						.valueOf(oriselOrSuppleftnum)+",supptotalnum:"+String
						.valueOf(oriselOrSupptotalnum);
			}
		
		}
		return result;
	}
	//处理注水
	public static String handlerOriWf(String type,int originalgoodswfVal,int oriselOrSuppwfval){
		String result = null;
		if(!StringUtils.isEmpty(type)) {
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				result = "goodswf:"+String
						.valueOf(originalgoodswfVal);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				result = (originalgoodswfVal!=0)?"goodswf:"+String
						.valueOf(originalgoodswfVal):"";
						
				result = StringUtils.isEmpty(result)?"selwf:"+String
						.valueOf(oriselOrSuppwfval):result+",selwf:"+String
						.valueOf(oriselOrSuppwfval);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				result = (originalgoodswfVal!=0)?"goodswf:"+String
						.valueOf(originalgoodswfVal):"";
				result = StringUtils.isEmpty(result)?"suppwf:"+String
						.valueOf(oriselOrSuppwfval):result+",suppwf:"+String
						.valueOf(oriselOrSuppwfval);
			}
			
		}
		return result;
	}
	public static String handlerItem(String type,String goodsId,String id){
		String result = null;
		if(!StringUtils.isEmpty(type)) {
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				result = goodsId;
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				if(!StringUtils.isEmpty(goodsId)&&Long.valueOf(goodsId)>0) {
					result = goodsId;
				}
				result = StringUtils.isEmpty(result)?String
						.valueOf(id):result+",selid:"+String
						.valueOf(id);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				if(!StringUtils.isEmpty(goodsId)&&Long.valueOf(goodsId)>0) {
					result = goodsId;
				}
				result = StringUtils.isEmpty(result)?String
						.valueOf(id):result+",suppid:"+String
						.valueOf(id);
			}
			
		}
		return result;
	}
	
	
	/**
     * 多个分店id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSuppliersInventoryString(List<GoodsSuppliersDO> rgsiList) {
		StringBuffer sb  = new StringBuffer();
		//i.首先保存商品id与选型id的关联关系
		for(GoodsSuppliersDO rgsi:rgsiList) {
			sb.append(rgsi.getSuppliersId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	/**
	 * 拼装返回选型或分店id的字符串 
	 * 以空格分隔
	 * @param goodsSelectionList
	 * @return
	 */
	public static String getIdsStringSelection(List<GoodsSelectionModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(GoodsSelectionModel ogsm:goodsSelectionList) {  
				if (ogsm.getId()!= null
						&& ogsm.getId()> 0) { //商品选型的id,多个选型id以空格间隔的字符串
					sb.append(ogsm.getId());
					sb.append(",[调整数量:"+ogsm.getNum()+"]");
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
	public static String getGoodsIds(List<Long> goodsIds) {
		StringBuffer sb  = new StringBuffer();
		
		for(Long goodsId:goodsIds) {  
			if (goodsId!= null
					&& goodsId> 0) { //商品id,多个id以空格间隔的字符串
				sb.append(goodsId);
				sb.append(String.valueOf((char) 29));
			}
			
		}
		
		return sb.toString();
	}
	
	
	public static String getIdsStringSuppliers(List<GoodsSuppliersModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(GoodsSuppliersModel ogsm:goodsSelectionList) {  
				if (ogsm.getSuppliersId()!= null
						&& ogsm.getSuppliersId()> 0) { //商品选型的id,多个选型id以空格间隔的字符串
					sb.append(ogsm.getSuppliersId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
}
