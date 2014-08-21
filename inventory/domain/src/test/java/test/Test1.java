package test;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
public class Test1 {
	
	public static void main(String[] args) throws ParseException {
		//SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//Date startDate = dateFormatter.parse("2010/11/28 01:06:00");
		//CopyOfLogsEventScheduled log = new  CopyOfLogsEventScheduled();
		 //log.execFixedRate4Logs();
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask(){
		   public void run() {
			  
		       System.out.println("execute task!"+ this.scheduledExecutionTime());
		   }
		},3000,4 * 1000);*/
		//System.out.println("当前时间="+TimeUtil.getNowTimestamp10Long());
		//System.out.println("5分钟前时间="+Test1.getBeforXTimestamp10Long(5));
		//System.out.println("当前时间="+TimeUtil.dateFormat(TimeUtil.getNowTimestamp10Long()));
		//System.out.println("5分钟前时间="+TimeUtil.dateFormat(Test1.getBeforXTimestamp10Long(5)));
		//System.out.println("相差="+TimeUtil.getDayDifferenceByTS(TimeUtil.getNowTimestamp10Long(), Test1.getBeforXTimestamp10Long(5)));
		
		
	
		try {
			
			//Test1.testReHash();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		System.out.println("list="+list.indexOf("1"));
		//System.out.println("slot="+6%4);
		
		
	}
	
	/**
	 * 获取几分钟前的时间
	 * @return
	 */
	public static long getBeforXTimestamp10Long(int minute) {
		long curTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(curTime);
		cal.add(10, -8);
		cal.add(Calendar.MINUTE, (-minute));// 5分钟之前的时间
		String str = String.valueOf(cal.getTime().getTime()).substring(0, 10);
		new Long(0L);
		return Long.parseLong(str);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void testReHash() throws Exception
	{
	  Integer localInteger = 0;
	  HashMap localHashMap = new HashMap();
	  ArrayList localArrayList = new ArrayList();
	 
	  String[] logicServers = { "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "sa", "sb" };
	  String[] logicServers2 = { "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "sa", "sb", "sc" };
	  float f1 = 0.0F; float f2 = 0.0F; float f3 = 100000.0F;
	 
	  for (int i = 0; i < f3; ++i) {
		Integer abs=  Integer.valueOf(Math.abs(new Random().nextInt()));
		if(abs<0||(abs>0&&abs<=1))
		 System.out.println("abs="+abs);
	    localArrayList.add(abs);
	  
	  }
	 
	  for (Iterator localIterator = localArrayList.iterator(); localIterator.hasNext(); ) { 
	  	 localInteger = (Integer)localIterator.next();
	    localHashMap.put(localInteger, logicServers[(localInteger.intValue() % logicServers.length)]);
	  }
	 
	  for (Iterator localIterator = localArrayList.iterator(); localIterator.hasNext(); ) { 
	    localInteger = (Integer)localIterator.next();
	    String str = logicServers2[(localInteger.intValue() % logicServers2.length)];
	 
	    if (str.equals(localHashMap.get(localInteger))) {
	      f1 += 1.0F;
	    }
	    else {
	      f2 += 1.0F;
	    }
	  }
	 
	  System.out.println(new StringBuilder().append(logicServers.length).append("|").append(logicServers2.length).append(", same=").append(f1).append(" samepercent= ").append(f1 / f3 * 100.0F).append("%, diff=").append(f2).toString());
	}
}