package test;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gome.domain.repository.DemoDomainRepository;





public class ServiceTest extends AbstractTest {
	 private static Logger logger = Logger.getLogger(ServiceTest.class);
	 @Autowired DemoDomainRepository demoDomainRepository ;
	 
	 @Test
	    public void countAll(){
	        logger.info("数据库中的记录条数:"  + demoDomainRepository.countAll());
	        //System.out.println("数据库中的记录条数:"  + userService.countAll());
	    }
	    
	
}
