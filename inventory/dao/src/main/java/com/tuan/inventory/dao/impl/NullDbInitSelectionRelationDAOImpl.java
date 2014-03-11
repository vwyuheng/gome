package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.NullDbInitSelectionRelationDAO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;

public class NullDbInitSelectionRelationDAOImpl  extends SqlMapClientDaoSupport  implements
		NullDbInitSelectionRelationDAO {

	@Override
	public int insertSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo) {
		return (Integer) super.getSqlMapClientTemplate().insert("insertSelectionRelation", rgsrDo);
	}

	@Override
	public int updateSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteSelectionRelationById(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

}
