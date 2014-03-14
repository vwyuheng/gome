package com.tuan.inventory.domain.support.jedistools;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * A simple factory that returns a {@link Jedis} object back 
 * to the caller of {@link JedisFactory#getInstance()} after 
 * which they can call {@link JedisFactory#getRes()} to get 
 * a {@link Jedis} object and {@link JedisFactory#returnRes(Jedis)} to 
 * return the resource back to the pool.
 * 
 * @author henry.yu
 * @date 2014/3/14
 */
public class JedisFactory 
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
	 * Static for Redis' minus infinity[����].
	 */
	public static String MINUS_INF = "-inf";
	/**
	 * Static for Redis'  plus infinity.
	 */
	public static String PLUS_INF = "+inf";
	/**
	 * Maximum number of allowed Jedis active connections in the pool.
	 */
	protected static int REDIS_POOL_MAX_ACTIVE = 200;
	/**
	 * Max number of allowed idle Jedis connections in the pool.
	 */
	protected static int REDIS_POOL_MAX_IDLE = REDIS_POOL_MAX_ACTIVE;
	
	/**
	 * Number of times to try to get resources before giving up and reconnecting the entire pool.
	 */
	protected static int REDIS_FAILED_RESOURCES_BEFORE_RECONNECT = REDIS_POOL_MAX_ACTIVE / 2 + 1;
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
	 * the server. The {@link Jedis} object must be returned back using {@link JedisFactory#returnRes(Jedis)} so others can use it..
	 * 
	 * @return the Jedis object use, must be returned back using {@link JedisFactory#returnRes(Jedis)} so others can use it. 
	 */
	public static Jedis getRes() 
	{
		return getWorkingResource();
	}
	
	/**
	 * Returns you a working resource or null if none are found.
	 * 
	 * @return the working {@link Jedis} resource.
	 */
	protected static Jedis getWorkingResource()
	{
		// try to find a working resource
	    for (int i = 0; i < REDIS_FAILED_RESOURCES_BEFORE_RECONNECT; i++)
	    {
	    	Jedis j = jf.jedisSentinelPool.getResource();
	    
	    	if (j.isConnected())				
	    	{
	    		return j;
	    	}
	    	else 
	    	{
	    		jf.jedisSentinelPool.returnBrokenResource(j);	    
	    	}
	    }
	    
	    return null;
	}
	
	/**
	 * Returns the given {@link Jedis} object back to the connection pool so it can  be reused.
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
    
    public <T>  T withJedisDo(JWork<T> work)
    {
    	// catch exception and gracefully fall back.    	
    	try 
    	{
    		Jedis j = getRes();
    		T ret = work.work(j);
    		returnRes(j);
    	
    		return ret;
    	}
    	catch (Exception e)
    	{
    		return null;
    	}
    }        
    
    public interface Work<Return,Param>
    {
    	public Return work(Param p);
    }
    
    public interface JWork<Return> extends Work<Return, Jedis>
    {
    	
    }
}
