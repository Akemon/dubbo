package com.hk.dubbo_common.service;


import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.User;

/**
 * @Author khe
 * @Date 2019/1/29
 */
public interface UserService {

	// 登录
	ServerResponse<User> login(String username, String password);

	// 注册
	ServerResponse<String> register(User user);

	// 校验参数
	ServerResponse<String> checkValid(String str, String type);

	// 获取忘记密码的问题
	ServerResponse<String> getForgetPasswordQuestion(String username);

	// 检查用户是否回答问题正确
	ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

	// 根据用户名，密码，token设定新的密码
	ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

	// 登录状态下的重置密码
	ServerResponse<String> resetPassword(String passswordOld, String passwordNew, User user);

	//更新个人信息
	ServerResponse<User> updateInformation(User user);

	//获取用户个人信息
	ServerResponse<User> getUserInformation(Integer id);

	//判断是否为一个管理员
	ServerResponse checkAdminRole(User user);


}
