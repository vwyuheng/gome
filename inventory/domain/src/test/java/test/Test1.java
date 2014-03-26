package test;
import java.text.ParseException;

import com.tuan.inventory.domain.job.event.CopyOfLogsEventScheduled;
public class Test1 {
	
	public static void main(String[] args) throws ParseException {
		//SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//Date startDate = dateFormatter.parse("2010/11/28 01:06:00");
		CopyOfLogsEventScheduled log = new  CopyOfLogsEventScheduled();
		 log.execFixedRate4Logs();
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask(){
		   public void run() {
			  
		       System.out.println("execute task!"+ this.scheduledExecutionTime());
		   }
		},3000,4 * 1000);*/
	}
	
}