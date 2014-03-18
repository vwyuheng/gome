package com.tuan.inventory.domain.support.enu;
/**
 * 用于定义库存变化量数据结构>key
 * @author henry.yu
 * @date 2014/3/18
 */
public enum InventoryVarQttEnum {
	
	num("商品总库存变化量"),
	leftNum("商品总库存剩余量");
	//selectionNum("选型商品库存变化量"),
	//selectionLeftNum("选型商品库存剩余量"),
	//supplierNum("分店商品库存变化量");
	//supplierLeftNum("分店商品库存剩余量");
	
	private String description;
	
	private InventoryVarQttEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static void main(String[] args) {
		System.out.println(InventoryVarQttEnum.num);
	}
}

