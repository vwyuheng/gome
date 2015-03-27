package com.gome.domain.repository;

import javax.annotation.Resource;

import com.gome.dao.UserDao;

public class DemoDomainRepository extends AbstractRepository {
	@Resource
	private UserDao  userDao;
	
}
