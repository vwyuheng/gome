package test;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import test.runner.JUnit4ClassLog4jRunner;

import com.gome.core.common.datasource.DataSourceContextHolder;
import com.gome.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.gome.dao.UserDao;
import com.gome.dao.data.User;

@RunWith(JUnit4ClassLog4jRunner.class)
@ContextConfiguration(locations = "classpath:/bean/quickstart-dao-env-test.xml")
//@Transactional
public class insertTest {
    
    protected static Log logger = LogFactory.getLog(insertTest.class);
    //@Autowired UserService userService ;
    @Autowired UserDao userDao ;
    
    
    @Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
    
    @Test
    public void countAll(){
        logger.info("数据库中的记录条数:"  + userDao.countAll());
        //System.out.println("数据库中的记录条数:"  + userDao.countAll());
    }
    
    @Test
    public void insert(){
        User user = new User();
        user.setUsername("于恒");
        user.setPassword("passtest");
        user.setEmail("dennisit@163.com");
        user.setSex("男");
        user.setAge(23);
        userDao.insert(user);
    }
    
    @Test
    public void selectAll(){
        List<User> list = userDao.selectAll();
        for(int i=0; i<list.size(); i++){
            User user = list.get(i);
            System.out.println("用户名:" + user.getUsername() + "\t密码:" + user.getPassword() + "\t邮箱：" + user.getEmail());
        }
    }
    
    @Test
    public void update(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("xxxxxxxx");
        user.setEmail("xxxxxx@163xxx");
        user.setSex("男");
        user.setAge(23);
        userDao.update(user);
    }
    
    @Test
    public void delete(){
        userDao.delete("test");
    }
    
    @Test
    public void findByName(){
        User user = userDao.findByUserName("test");
        System.out.println("用户名:" + user.getUsername() + "\t密码:" + user.getPassword() + "\t邮箱：" + user.getEmail());

    }
}