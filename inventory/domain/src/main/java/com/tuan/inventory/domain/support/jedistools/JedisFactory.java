package com.tuan.inventory.domain.support.jedistools;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.tuan.inventory.domain.support.exception.CacheRunTimeException;

/**
 * redis组件:模板方法,统一管理redis连接资源的申请与释放
 * @author henry.yu
 * @date 2014/3/14
 */
public class JedisFactory extends RedisBaseObject
{
	//protected static Logger m_logger = Logger.getLogger(JedisFactory.class.getName());
	protected static Log m_logger = LogFactory.getLog(JedisFactory.class.getName());
	/**
	 * spring 注入的 The connection pool.
	 */
	@Resource 
	JedisSentinelPool jedisSentinelPool;
	/**
	 * spring 静态注入的解决方式
	 * The connection pool.
	 */
	protected static JedisFactory jf;
    /**
     * 解决spring静态注入问题
     */
	@PostConstruct
	public void init() {
		jf = this;
		jf.jedisSentinelPool = this.jedisSentinelPool;
	}
	/**
	 * 静态注入时，spring需要给jedisSentinelPool属性set值
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
	 * 获取有效连接
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
	 * 将连接返回池子
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
     * 摧毁无效链接
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
    	}catch (CacheRunTimeException ce) {  //捕获自定义运行时异常
    		returnBrokenRes(j);
    		j = null;
    		m_logger.error("jedisFactory.withJedisDo invoke error", ce);
    		return null;
    	}
    	catch (Exception e)  //其他异常
    	{
    		returnBrokenRes(j);
    		j = null;
    		m_logger.error("jedisFactory.withJedisDo invoke error", e);
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
