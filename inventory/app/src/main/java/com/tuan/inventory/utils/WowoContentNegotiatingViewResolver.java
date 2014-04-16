package com.tuan.inventory.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.util.UrlPathHelper;

/**
 * @description
 * Wowo自定义内容协商器
 * @author tianzq
 * @date 2013.11.20
 */
public class WowoContentNegotiatingViewResolver extends
		ContentNegotiatingViewResolver {
	private static final Log logger = LogFactory.getLog(WowoContentNegotiatingViewResolver.class);
	private static final UrlPathHelper urlPathHelper = new UrlPathHelper();
	private boolean favorUrlParamPath = true;
	private ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap<String, MediaType>();
	public WowoContentNegotiatingViewResolver() {
		super();
		super.setFavorPathExtension(false);//默认设置为false
	}
	
	/**
	 * Determines the list of {@link MediaType} for the given {@link HttpServletRequest}.
	 * <p>The default implementation invokes {@link #getMediaTypeFromFilename(String)} if {@linkplain
	 * #setFavorPathExtension(boolean) favorPathExtension} property is <code>true</code>. If the property is
	 * <code>false</code>, or when a media type cannot be determined from the request path, this method will
	 * inspect the {@code Accept} header of the request.
	 * <p>This method can be overriden to provide a different algorithm.
	 * @param request the current servlet request
	 * @return the list of media types requested, if any
	 */
	protected List<MediaType> getMediaTypes(HttpServletRequest request) {
		if (this.favorUrlParamPath) {
			String requestUri = urlPathHelper.getRequestUri(request);
			Iterator<String> it=this.mediaTypes.keySet().iterator();
			if(it!=null){
				String key="";
				while(it.hasNext()){
					key=it.next();
					if(requestUri.indexOf(key)>-1){
						return Collections.singletonList(this.mediaTypes.get(key));
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Requested media type is ' Null ' (based on uri '" + requestUri + "')");
				}
			}
		}
		return super.getMediaTypes(request);
		
	}
	public boolean isFavorUrlParamPath() {
		return favorUrlParamPath;
	}
	public void setFavorUrlParamPath(boolean favorUrlParamPath) {
		this.favorUrlParamPath = favorUrlParamPath;
	}
	
	public void setMediaTypes(Map<String, String> mediaTypes) {
		super.setMediaTypes(mediaTypes);
		for (Map.Entry<String, String> entry : mediaTypes.entrySet()) {
			String extension = entry.getKey().toLowerCase(Locale.ENGLISH);
			MediaType mediaType = MediaType.parseMediaType(entry.getValue());
			this.mediaTypes.put(extension, mediaType);
		}
	}
}
