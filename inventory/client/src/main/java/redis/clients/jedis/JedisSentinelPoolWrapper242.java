package redis.clients.jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import com.tuan.inventory.client.support.utils.Utils;

public class JedisSentinelPoolWrapper242 extends Pool<Jedis> {

    protected GenericObjectPoolConfig poolConfig;

    protected int timeout = Protocol.DEFAULT_TIMEOUT;

    protected String password;

    protected int database = Protocol.DEFAULT_DATABASE;

    protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();

    protected Logger log = Logger.getLogger(getClass().getName());

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig) {
	this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null,
		Protocol.DEFAULT_DATABASE);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels) {
	this(masterName, sentinels, new GenericObjectPoolConfig(),
		Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    String password) {
	this(masterName, sentinels, new GenericObjectPoolConfig(),
		Protocol.DEFAULT_TIMEOUT, password);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, int timeout,
	    final String password) {
	this(masterName, sentinels, poolConfig, timeout, password,
		Protocol.DEFAULT_DATABASE);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, final int timeout) {
	this(masterName, sentinels, poolConfig, timeout, null,
		Protocol.DEFAULT_DATABASE);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, final String password) {
	this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT,
		password);
    }

    public JedisSentinelPoolWrapper242(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, int timeout,
	    final String password, final int database) {
	this.poolConfig = poolConfig;
	this.timeout = timeout;
	this.password = password;
	this.database = database;

	//HostAndPort master = initSentinels(sentinels, masterName);
	//initPool(master);
	//初始化slave
	HostAndPort slave = initSentinelsSlave(sentinels, masterName);
	initSlavePool(slave);
    }

    public void returnBrokenResource(final Jedis resource) {
	returnBrokenResourceObject(resource);
    }

    public void returnResource(final Jedis resource) {
	resource.resetState();
	returnResourceObject(resource);
    }

    private volatile HostAndPort currentHostMaster;

    public void destroy() {
	for (MasterListener m : masterListeners) {
	    m.shutdown();
	}

	super.destroy();
    }

    public HostAndPort getCurrentHostMaster() {
	return currentHostMaster;
    }

    private void initPool(HostAndPort master) {
	if (!master.equals(currentHostMaster)) {
	    currentHostMaster = master;
	    log.info("Created JedisPool to master at " + master);
	    initPool(poolConfig,
		    new JedisFactory(master.getHost(), master.getPort(),
			    timeout, password, database));
	}
    }

    /**
     * 初始化slave连接池
     * @param slave
     */
    private void initSlavePool(HostAndPort slave) {
    	if (!slave.equals(currentHostMaster)) {
    		currentHostMaster = slave;
    		log.info("Created JedisPool to slave at " + slave);
    		initPool(poolConfig,
    				new JedisFactory(slave.getHost(), slave.getPort(),
    						timeout, password, database));
    	}
    }
    
    
    private HostAndPort initSentinels(Set<String> sentinels,
	    final String masterName) {

	HostAndPort master = null;
	boolean running = true;

	outer: while (running) {

	    log.info("Trying to find master from available Sentinels...");

	    for (String sentinel : sentinels) {

		final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
			.split(":")));

		log.fine("Connecting to Sentinel " + hap);

		try {
		    Jedis jedis = new Jedis(hap.getHost(), hap.getPort());

		    if (master == null) {
			master = toHostAndPort(jedis
				.sentinelGetMasterAddrByName(masterName));
			log.fine("Found Redis master at " + master);
			jedis.disconnect();
			break outer;
		    }
		} catch (JedisConnectionException e) {
		    log.warning("Cannot connect to sentinel running @ " + hap
			    + ". Trying next one.");
		}
	    }

	    try {
		log.severe("All sentinels down, cannot determine where is "
			+ masterName + " master is running... sleeping 1000ms.");
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	log.info("Redis master running at " + master
		+ ", starting Sentinel listeners...");

	for (String sentinel : sentinels) {
	    final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
		    .split(":")));
	    MasterListener masterListener = new MasterListener(masterName,
		    hap.getHost(), hap.getPort());
	    masterListeners.add(masterListener);
	    masterListener.start();
	}

	return master;
    }

    private HostAndPort initSentinelsSlave(Set<String> sentinels,
    		final String masterName) {
    	
    	HostAndPort master = null;
    	HostAndPort isslave = null;
    	boolean running = true;
    	List<HostAndPort> listSlaves = new ArrayList<HostAndPort>();
    	List<Map<String, String>> slaves = null;
    	outer: while (running) {
    		
    		log.info("Trying to find master from available Sentinels...");
    		
    		for (String sentinel : sentinels) {
    			
    			final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
    					.split(":")));
    			
    			log.fine("Connecting to Sentinel " + hap);
    			
    			try {
    				Jedis jedis = new Jedis(hap.getHost(), hap.getPort());
    				
    				if (master == null) {
    					master = toHostAndPort(jedis
    							.sentinelGetMasterAddrByName(masterName));
    					log.fine("Found Redis master at " + master);
    					if(master!=null) {
    						//以下是处理slaves的
    						List<String> hostAndPortSalve = null;
    						slaves = jedis.sentinelSlaves(masterName);
    						if (!CollectionUtils.isEmpty(slaves)) {
    							for (Map<String, String> map : slaves) {
    								/*System.out.println("slave=" + map);
    								System.out.println("slavename="
    										+ map.get("name"));
    								System.out.println("slaveport="
    										+ Integer.parseInt(map
    												.get("port")));
    								System.out.println("slaveip="
    										+ map.get("ip"));*/
    								hostAndPortSalve = new ArrayList<String>();
    								hostAndPortSalve.add(map.get("ip"));
    								hostAndPortSalve.add(map.get("port"));

    								listSlaves.add(toHostAndPort(hostAndPortSalve));
    							}
    							
    						}else {  //不存在slave时,maser充当slave
    							listSlaves.add(master);
    						}
    					}
						jedis.disconnect();
    					break outer;
    				}
    			} catch (JedisConnectionException e) {
    				log.warning("Cannot connect to sentinel running @ " + hap
    						+ ". Trying next one.");
    			}
    		}
    		
    		try {
    			log.severe("All sentinels down, cannot determine where is "
    					+ masterName + " master is running... sleeping 1000ms.");
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	log.info("Redis master running at " + master
    			+ ", starting Sentinel listeners...");
    	
    	for (String sentinel : sentinels) {
    		final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
    				.split(":")));
    		MasterListener masterListener = new MasterListener(masterName,
    				hap.getHost(), hap.getPort());
    		masterListeners.add(masterListener);
    		masterListener.start();
    	}
    	if(!CollectionUtils.isEmpty(listSlaves)) {
    		//随机取一个slave
    		isslave = listSlaves.get(Utils.random(listSlaves.size()));
    		log.fine("Found Redis slave at " + isslave);
		}
    	
    	
    	//return master;
    	return isslave;
    }
    
    private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
	String host = getMasterAddrByNameResult.get(0);
	int port = Integer.parseInt(getMasterAddrByNameResult.get(1));

	return new HostAndPort(host, port);
    }

    protected class JedisPubSubAdapter extends JedisPubSub {
	@Override
	public void onMessage(String channel, String message) {
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
	}
    }

    protected class MasterListener extends Thread {

	protected String masterName;
	protected String host;
	protected int port;
	protected long subscribeRetryWaitTimeMillis = 5000;
	protected Jedis j;
	protected AtomicBoolean running = new AtomicBoolean(false);

	protected MasterListener() {
	}

	public MasterListener(String masterName, String host, int port) {
	    this.masterName = masterName;
	    this.host = host;
	    this.port = port;
	}

	public MasterListener(String masterName, String host, int port,
		long subscribeRetryWaitTimeMillis) {
	    this(masterName, host, port);
	    this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
	}

	public void run() {

	    running.set(true);

	    while (running.get()) {

		j = new Jedis(host, port);

		try {
		    j.subscribe(new JedisPubSubAdapter() {
			@Override
			public void onMessage(String channel, String message) {
			    log.fine("Sentinel " + host + ":" + port
				    + " published: " + message + ".");

			    String[] switchMasterMsg = message.split(" ");

			    if (switchMasterMsg.length > 3) {

				if (masterName.equals(switchMasterMsg[0])) {
				    initPool(toHostAndPort(Arrays.asList(
					    switchMasterMsg[3],
					    switchMasterMsg[4])));
				} else {
				    log.fine("Ignoring message on +switch-master for master name "
					    + switchMasterMsg[0]
					    + ", our master name is "
					    + masterName);
				}

			    } else {
				log.severe("Invalid message received on Sentinel "
					+ host
					+ ":"
					+ port
					+ " on channel +switch-master: "
					+ message);
			    }
			}
		    }, "+switch-master");

		} catch (JedisConnectionException e) {

		    if (running.get()) {
			log.severe("Lost connection to Sentinel at " + host
				+ ":" + port
				+ ". Sleeping 5000ms and retrying.");
			try {
			    Thread.sleep(subscribeRetryWaitTimeMillis);
			} catch (InterruptedException e1) {
			    e1.printStackTrace();
			}
		    } else {
			log.fine("Unsubscribing from Sentinel at " + host + ":"
				+ port);
		    }
		}
	    }
	}

	public void shutdown() {
	    try {
		log.fine("Shutting down listener on " + host + ":" + port);
		running.set(false);
		// This isn't good, the Jedis object is not thread safe
		j.disconnect();
	    } catch (Exception e) {
		log.severe("Caught exception while shutting down: "
			+ e.getMessage());
	    }
	}
    }
}