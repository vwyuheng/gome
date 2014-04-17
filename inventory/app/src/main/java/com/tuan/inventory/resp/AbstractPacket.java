package com.tuan.inventory.resp;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @description
 * 统一的响应报文头
 * @author tianzq
 * @date 2013.11.20
 */
@XmlRootElement(name = "packet")
public abstract class AbstractPacket implements Serializable {

	private static final long serialVersionUID = 1677345171490009442L;

}
