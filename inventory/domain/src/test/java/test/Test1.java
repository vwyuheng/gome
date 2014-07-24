package test;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
		
		
		List<Long> selectionIdList = new ArrayList<Long>();
		selectionIdList.add(28l);
		selectionIdList.add(29l);
		selectionIdList.add(30l);
		
		//System.out.println(LogUtil.formatListLog(selectionIdList));
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
}