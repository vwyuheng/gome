package com.tuan.inventory.domain.support.jedistools;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.tuan.inventory.domain.support.exception.CacheRunTimeException;

/**
 * redis���:ģ�巽��,ͳһ����redis������Դ���������ͷ�
 * @author henry.yu
 * @date 2014/3/14
 */
public class JedisFactory extends RedisBaseObject
{
	protected static Logger m_logger = Logger.getLogger(JedisFactory.class.getName());
	/**
	 * spring ע��� The connection pool.
	 */
	@Resource 
	JedisSentinelPool jedisSentinelPool;
	/**
	 * spring ��̬ע��Ľ����ʽ
	 * The connection pool.
	 */
	protected static JedisFactory jf;
    /**
     * ���spring��̬ע������
     */
	@PostConstruct
	public void init() {
		jf = this;
		jf.jedisSentinelPool = this.jedisSentinelPool;
	}
	/**
	 * ��̬ע��ʱ��spring��Ҫ��jedisSentinelPool����setֵ
	 * @param jedisSentinelPool
	 */
	public void setJedisSentinelPool(JedisSentinelPool jedisSentinelPool) {
		this.jedisSentinelPool = jedisSentinelPool;
	}
	/**
	 *  Prevent direct access to the constructor 
	 */
	private JedisFactory() 
	{
		super();	
	}

	/**
	 * Gives the user access to a Redis object that they can use to interact with 
	 */
	public static Jedis getRes() 
	{
		return getWorkingResource();
	}
	
	/**
	 * ��ȡ��Ч����
	 * 
	 * @return the working {@link Jedis} resource.
	 */
	protected static Jedis getWorkingResource()
	{
		Jedis j = null;
		// try to find a working resource
	    for (int i = 0; i < REDIS_FAILED_RESOURCES_BEFORE_RECONNECT; i++)
	    {
			try {
				j = jf.jedisSentinelPool.getResource();
				if (j.isConnected()) {
					return j;
				} else {
					returnBrokenRes(j);
				}
			} catch (JedisConnectionException e) {
				if (j != null)
					returnBrokenRes(j);
			}
		}
	    
	    return null;
	}
	
	/**
	 * �����ӷ��س���
	 * 
	 * @param res the object to return
	 */
    public static void returnRes(Jedis res)
    {
    	if (jf.jedisSentinelPool != null)
    	{
    		jf.jedisSentinelPool.returnResource(res);
    	}
    }
    /**
     * �ݻ���Ч����
     * @param res
     */
    public static void returnBrokenRes(Jedis res)
    {
    	if (jf.jedisSentinelPool != null)
    	{
    		jf.jedisSentinelPool.returnBrokenResource(res);
    	}
    }
    
    public <T>  T withJedisDo(JWork<T> work)
    {
    	// catch exception and gracefully fall back.  
    	Jedis j = null;
    	try 
    	{
    		j = getRes();
    		T ret = work.work(j);
    		returnRes(j);
    		return ret;
    	}catch (CacheRunTimeException ce) {  //�����Զ�������ʱ�쳣
    		returnBrokenRes(j);
    		j = null;
    		m_logger.error("jedisFactory.withJedisDo invoke error", ce);
    		ce.printStackTrace();
    		return null;
    	}
    	catch (Exception e)  //�����쳣
    	{
    		returnBrokenRes(j);
    		j = null;
    		m_logger.error("jedisFactory.withJedisDo invoke error", e);
    		e.printStackTrace();
    		return null;
    	}/*finally {
    		if(j!=null) 
    			returnRes(j);
    	}*/
    }        
    
    public interface Work<Return,Param>
    {
    	public Return work(Param p) throws Exception;
    }
    
    public interface JWork<Return> extends Work<Return, Jedis>
    {
    	
    }
}
