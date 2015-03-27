package com.gome.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import com.gome.dao.UserDao;
import com.gome.dao.data.User;
import com.gome.domain.parent.repository.AbstractRepository;

public class DemoDomainRepository extends AbstractRepository {
	@Resource
	private UserDao  userDao;
	
	    public int countAll() {
	        return this.userDao.countAll();
	    }

	    public int delete(String userName) {
	        return this.userDao.delete(userName);
	    }

	    public User findByUserName(String userName) {
	        return this.userDao.findByUserName(userName);
	    }

	    public int insert(User user) {
	        return this.userDao.insert(user);
	    }

	    public List<User> selectAll() {
	        return this.userDao.selectAll();
	    }

	    public int update(User user) {
	         return this.userDao.update(user);
	    }
	    
	    public UserDao getUserDao() {
	        return userDao;
	    }

	    public void setUserDao(UserDao userDao) {
	        this.userDao = userDao;
	    }

}
