package com.tuan.inventory.client.support.jedistools;


/**
 * Base class for Java objects that wrap Redis data 
 * types with a Java native API.
 * 
 * @author henry.yu
 * @date 2014/3/14
 *
 */
public abstract class RedisBaseObject
{		
	/**
	 * Static for Redis' minus infinity[无穷].
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
	 * Returns the key used when interacting with 
	 * the store keys in Redis. To be implemented 
	 * by the child.
	 * 
	 * @return a String representing the key
	 */
	//protected abstract String getKey();

	//protected String REDIS_STORE_VERSION = "0";
	
	//protected String REDIS_STORE_PREFIX = "rs:" + REDIS_STORE_VERSION;
	
	//protected String m_strFullKey;
	
	/*protected String getFullKey()
	{
		if (m_strFullKey == null)
		{
			m_strFullKey = REDIS_STORE_PREFIX + ":" + getKey();
		}
		
		return m_strFullKey; 
	}		*/
}
