package com.hk.dubbo_controller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import req.UserLoginReq;
import service.user.UserService;

/**
 * @Author khe
 * @Date 2019/1/31
 */
@RestController
@RequestMapping("/")
public class UserController {

	@Reference(version = "1.0.0")
	private UserService userService;

	@GetMapping("login.do")
	public User login(){
		return userService.login(new UserLoginReq("root","123"));
	}
}
