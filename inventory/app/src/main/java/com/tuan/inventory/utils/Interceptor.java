package com.tuan.inventory.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageResultEnum;

/**
 * @description Wowo自定义拦截器
 * @author tianzq
 * @date 2013.11.25
 */
public class Interceptor extends HandlerInterceptorAdapter {
	//private static Logger logger = Logger.getLogger("INTERFACE.LOG");
	private static Log logger = LogFactory.getLog("INTERFACE.LOG");
	private Set<String> resultPrefixFilter;
	private boolean isFilter = false;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LogModel lm = null;
		Message messageRoot = null;
		String traceRootId = request.getParameter("traceRootId");
		String traceId = request.getParameter("traceId");
		if(traceRootId == null || traceRootId.isEmpty()||traceId==null||traceId.isEmpty()){
			messageRoot = TraceMessageUtil.newRootMessage();	//trace根
			lm = LogModel.newLogModel(messageRoot.getTraceHeader().getRootId());
		}else{
			lm = LogModel.newLogModel(traceRootId);
			messageRoot = WrapUtils.makeTraceMessageByParam(traceRootId,traceId);
		}
		lm.addMetaData("Params", readRequestParams(request)).setMethod(request.getRequestURI());
		if(logger.isInfoEnabled()){
			logger.info(lm.toJson(true));
		}
		request.setAttribute("lm", lm);
		request.setAttribute("messageRoot", messageRoot);
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		LogModel lm = (LogModel)request.getAttribute("lm");
		Message messageRoot = (Message)request.getAttribute("messageRoot");
		super.postHandle(request, response, handler, modelAndView);
		try {
			if (isFilter && modelAndView != null) {
				Iterator<String> it = resultPrefixFilter.iterator();
				String key = null;
				while (it.hasNext()) {
					key = it.next();
					if (modelAndView.getModelMap().containsKey(key)) {
						modelAndView.getModelMap().remove(key);
					}
				}
				lm.setMethod(request.getRequestURI())
				.addMetaData(modelAndView.getViewName(), filterModel(modelAndView.getModel()));
			}
			if (logger.isInfoEnabled()) {
				logger.info(lm.toJson());
				TraceMessageUtil.traceMessagePrintE(messageRoot, MessageResultEnum.SUCCESS);
			}
		} catch (Exception e) {
			lm.addMetaData("SysError", e.getMessage());
			logger.error(lm.toJson(), e);
			TraceMessageUtil.traceMessagePrintE(messageRoot, MessageResultEnum.FAIL);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private Map readRequestParams(HttpServletRequest request) {
		try {
			@SuppressWarnings("unchecked")
			Enumeration<String> enumer=request.getParameterNames();
			Map<String,String> params=new HashMap<String,String>();
			String key=null;
			String[] value=null;
			while(enumer.hasMoreElements()){
				key=enumer.nextElement();
				value=request.getParameterValues(key);
				params.put(key,StringUtils.arrayToDelimitedString(value, ",") );
			}
			return params;
		} catch (Exception e) {
			return null;
		}
	}
	
	private Object filterModel(Map<String, Object> model) {
		Map<String, Object> result = new HashMap<String, Object>(model.size());
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			if (!(entry.getValue() instanceof BindingResult)) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	public void setResultPrefixFilter(Set<String> resultPrefixFilter) {
		this.resultPrefixFilter = resultPrefixFilter;
		if (this.resultPrefixFilter != null && this.resultPrefixFilter.size() > 0) {
			isFilter = true;
		}
	}

}
