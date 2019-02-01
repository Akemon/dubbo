package com.hk.dubbo_user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import entity.User;
import req.UserLoginReq;
import service.user.UserService;

/**
 * @Author khe
 * @Date 2019/1/29
 */
@Service(version = "1.0.0")
@org.springframework.stereotype.Service
public class UserServiceImpl  implements UserService {

	@Override
	public User login(UserLoginReq req) {
		String name = req.getName();
		String pass = req.getPassword();
		System.out.println("userName:"+name);
		System.out.println("userPass:"+pass);
		if(name.equals("root")&&pass.equals("123")){
			return new User("root","123");
		}
		return new User();
	}
}
