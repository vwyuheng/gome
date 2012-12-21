package com.tuan.pmt.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.core.common.page.PageList;
import com.tuan.core.common.page.Paginator;
import com.tuan.pmt.dao.CpnCouponDAO;
import com.tuan.pmt.dao.data.CpnCouponDO;

public class CpnCouponDAOImpl extends SqlMapClientDaoSupport implements CpnCouponDAO{

	@Override
	public Integer update(CpnCouponDO cpnCouponDO) {
		Object obj = super.getSqlMapClientTemplate().update("updateCpnCouponByCouponId", cpnCouponDO);
		return (Integer)obj;
	}

	@Override
	public CpnCouponDO queryCpnCouponByCouponId(long couponId) {
		Object obj = super.getSqlMapClientTemplate().queryForObject("queryCpnCouponByCouponId", couponId);
		return (CpnCouponDO)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageList queryCpnCouponByUserId(long userId, List<Integer> statusList, String platType, int currPage, int pageSize) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("statusList", statusList);
        param.put("platType", platType);
        if (pageSize < 1) {
            pageSize = 1000;
        }
        param.put("pageSize", pageSize);

        // 得到要查询的页
        int tPageNum = currPage - 1;
        if (tPageNum < 0) {
            tPageNum = 0;
        }
        param.put("pageNum", new Integer(tPageNum * pageSize));

        Paginator paginator = new Paginator();
        paginator.setItemsPerPage(pageSize);
        paginator.setPage(currPage);

        PageList pageList = new PageList();
        pageList.setPaginator(paginator);

        // 查询条数
        paginator.setItems(((Integer) (super.getSqlMapClientTemplate())
                .queryForObject("queryCpnCouponByUserIdCount", param))
                .intValue());
        if ((paginator.getItems() > 0)
                && (paginator.getItems() <= tPageNum * pageSize)) {
            tPageNum = paginator.getLastPage() - 1;
            param.put("pageNum", new Integer(tPageNum * pageSize));
        }
        // 查询列表
        pageList.addAll(getSqlMapClientTemplate().queryForList("queryCpnCouponByUserId", param));
        return pageList;
	}

	@Override
	public CpnCouponDO queryCpnCouponByCode(String code) {
		Object obj = super.getSqlMapClientTemplate().queryForObject("queryCpnCouponByCode", code);
		return (CpnCouponDO)obj;
	}

    @SuppressWarnings("unchecked")
	@Override
    public List<CpnCouponDO> queryCouponsByUserIdAndBatchId(long userId,long batchId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("batchId", batchId);
        return super.getSqlMapClientTemplate().queryForList("queryCouponsByUserIdAndBatchId", paramMap);
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<CpnCouponDO> queryCpnCouponByOrderId(long orderId) {
		Object obj = super.getSqlMapClientTemplate().queryForList("queryCpnCouponByOrderId", orderId);
		return (List<CpnCouponDO>)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CpnCouponDO> queryCouponDOByOrderIdAndStatus(long orderId, long userId, List<Integer> statusList) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("userId", userId);
        paramMap.put("statusList", statusList);
		Object obj = super.getSqlMapClientTemplate().queryForList("queryCpnCouponByOrderIdAndStatusList", paramMap);
		return (List<CpnCouponDO>)obj;
	}

}
