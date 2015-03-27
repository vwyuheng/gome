package com.gome.dao.data;

import com.gome.core.common.lang.GomeBaseDO;

/**
 * demo
 */
public class User extends GomeBaseDO{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 5337799704874593534L;
	private int id;
    private String username;
    private String password;
    private String sex;
    private String email;
    private int age;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
     
}