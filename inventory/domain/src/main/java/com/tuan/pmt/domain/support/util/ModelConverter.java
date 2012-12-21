package com.tuan.pmt.domain.support.util;

import java.util.ArrayList;
import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;

public class ModelConverter {
	
	public static CpnCouponModel toCouponModel(CpnCouponDO cpnCouponDO,CpnCouponBatchDO cpnCouponBatchDO,
			List<CpnCouponBatchCatDO> cpnCouponBatchCatDOList,List<CpnCouponBatchCityDO> cpnCouponBatchCityDOList){
		if(cpnCouponDO == null){
			return null;
		}
		CpnCouponModel cpnCouponModel = new CpnCouponModel();
		cpnCouponModel.setCouponId(cpnCouponDO.getCouponId());
		cpnCouponModel.setBatchId(cpnCouponDO.getBatchId());
		cpnCouponModel.setPrefix(cpnCouponDO.getPrefix());
		cpnCouponModel.setCode(cpnCouponDO.getCode());
		
		 if(null != cpnCouponDO.getGenTime()){
		     cpnCouponModel.setGenTime(DateUtils.timestampToString(cpnCouponDO.getGenTime(),null));
	        }
	        if(null != cpnCouponDO.getBindTime()){
	            cpnCouponModel.setBindTime(DateUtils.timestampToString(cpnCouponDO.getBindTime    (),null));
	        }
	       
	        if(null != cpnCouponModel.getUsedTime()){
	            cpnCouponModel.setUsedTime(DateUtils.timestampToString(cpnCouponDO.getUsedTime(),null));
	        }
		cpnCouponModel.setUserId(cpnCouponDO.getUserId());
		cpnCouponModel.setOrderId(cpnCouponDO.getOrderId());
		cpnCouponModel.setStatus(cpnCouponDO.getStatus());
		
		if(null != cpnCouponModel.getInvalidTime()){
            cpnCouponModel.setInvalidTime(DateUtils.timestampToString(cpnCouponDO.getInvalidTime(),null));
        }
		cpnCouponModel.setInvalidAdminId(cpnCouponDO.getInvalidAdminId());
		cpnCouponModel.setInvalidReason(cpnCouponDO.getInvalidReason());
		if(cpnCouponBatchDO != null){
		    cpnCouponModel.setCpnCouponBatchModel(toModelFromCpnCouponBatchDO(
		    		cpnCouponBatchDO,cpnCouponBatchCatDOList,cpnCouponBatchCityDOList));
		}
		return cpnCouponModel;
	}
	
	public static CpnCouponBatchModel toModelFromCpnCouponBatchDO(CpnCouponBatchDO cpnCouponBatchDO,
			List<CpnCouponBatchCatDO> cpnCouponBatchCatDOList,List<CpnCouponBatchCityDO> cpnCouponBatchCityDOList){
        if(cpnCouponBatchDO == null){
            return null;
        }
        CpnCouponBatchModel model = new CpnCouponBatchModel();
        model.setBatchId       (cpnCouponBatchDO.getBatchId        ());           
        model.setAssignType    (cpnCouponBatchDO.getAssignType     ());           
        model.setName          (cpnCouponBatchDO.getName           ());           
        model.setBackendName   (cpnCouponBatchDO.getBackendName    ());    
        
        if(null != cpnCouponBatchDO.getGenTime()){
            model.setGenTime       (DateUtils.timestampToString(cpnCouponBatchDO.getGenTime    (),null));
        }
        if(null != cpnCouponBatchDO.getStartTime      ()){
            model.setStartTime     (DateUtils.timestampToString(cpnCouponBatchDO.getStartTime    (),null));
        }
       
        if(null != cpnCouponBatchDO.getEndTime        ()){
            model.setEndTime       (DateUtils.timestampToString(cpnCouponBatchDO.getEndTime    (),null));
        }
        model.setFaceValue     (cpnCouponBatchDO.getFaceValue      ());           
        model.setNum           (cpnCouponBatchDO.getNum            ());           
        model.setAdminId       (cpnCouponBatchDO.getAdminId        ());           
        model.setDepartment    (cpnCouponBatchDO.getDepartment     ());           
        model.setComment       (cpnCouponBatchDO.getComment        ()); 

        if(null != cpnCouponBatchDO.getInvalidTime        ()){
            model.setInvalidTime   (DateUtils.timestampToString(cpnCouponBatchDO.getInvalidTime    (),null));
        }
        model.setInvalidAdminId(cpnCouponBatchDO.getInvalidAdminId ());           
        model.setInvalidReason (cpnCouponBatchDO.getInvalidReason  ());           
        model.setUseTerminal   (cpnCouponBatchDO.getUseTerminal    ());           
        model.setBindLimit     (cpnCouponBatchDO.getBindLimit      ());           
        model.setGoodsPrice    (cpnCouponBatchDO.getGoodsPrice     ());           
        model.setGoodsSetAmount(cpnCouponBatchDO.getGoodsSetAmount ());           
        model.setGoodsIds      (cpnCouponBatchDO.getGoodsIds       ());           
        model.setFirstTime     (cpnCouponBatchDO.getFirstTime      ());           
        model.setBindPhone     (cpnCouponBatchDO.getBindPhone      ());           
        model.setUsedNum       (cpnCouponBatchDO.getUsedNum        ());           
        model.setBindNum       (cpnCouponBatchDO.getBindNum        ());           
        model.setFreezeNum     (cpnCouponBatchDO.getFreezeNum      ());           
        model.setInvalidNum    (cpnCouponBatchDO.getInvalidNum     ());           
        model.setRefundAmount  (cpnCouponBatchDO.getRefundAmount   ());           
        model.setUsedAmount    (cpnCouponBatchDO.getUsedAmount     ());           
        model.setUserNum       (cpnCouponBatchDO.getUserNum        ());           
        model.setGoodsAmount   (cpnCouponBatchDO.getGoodsAmount    ());           
        model.setStatus        (cpnCouponBatchDO.getStatus         ());           
        model.setAllCity       (cpnCouponBatchDO.getAllCity        ());           
        model.setAllCat        (cpnCouponBatchDO.getAllCat         ());         
        
        if(cpnCouponBatchCatDOList != null){
			List<Long> catList = new ArrayList<Long>();
			for(CpnCouponBatchCatDO cpnCouponBatchCatDO : cpnCouponBatchCatDOList){
				catList.add(cpnCouponBatchCatDO.getCatId());
			}
			model.setCatIdList(catList);
		}
		if(cpnCouponBatchCityDOList != null){
			List<Long> cityList = new ArrayList<Long>();
			for(CpnCouponBatchCityDO cpnCouponBatchCityDO : cpnCouponBatchCityDOList){
				cityList.add(cpnCouponBatchCityDO.getCityId());
			}
			model.setCityIdList(cityList);
		}
        return model;
    }



}
