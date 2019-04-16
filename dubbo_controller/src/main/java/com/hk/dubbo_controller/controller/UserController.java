package com.hk.dubbo_controller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.User;
import com.hk.dubbo_common.service.UserService;
import com.hk.dubbo_common.util.CookieUtil;
import com.hk.dubbo_common.util.JsonUtil;
import com.hk.dubbo_common.util.RedisShardPoolUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

//import javax.servlet.http.HttpSession;

/**
 * @author 何康
 * @date 2018/10/30 10:44
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Reference(version = "1.0.0")
    private UserService userService;

    /***
     * 登录
     * @param userName
     * @param password
     * @return
     */

    @PostMapping("login.do")
    @ResponseBody
    public ServerResponse<User> login(@RequestParam("username") String userName,
                                      @RequestParam("password") String password,
                                      HttpServletRequest request
            ,HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return ServerResponse.createBySuccess(user);
        }
        ServerResponse<User> response = userService.login(userName, password);
        if (response.isSuccess()) {
            String uuidString = UUID.randomUUID().toString();
            CookieUtil.writeToken(httpServletResponse, uuidString);
            RedisShardPoolUtil.setEx(uuidString,
                    JsonUtil.object2String(response.getData()), 60 * 30);
        }
        return response;
    }


    /***
     * 登出
     * @param
     * @return
     */

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request,
                                         HttpServletResponse response) {
        String loginToken = CookieUtil.readToken(request);
        CookieUtil.delLoginToken(request, response);
        if (loginToken != null)
            RedisShardPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess("退出成功");
    }

    /***
     * 注册
     * @param user
     * @return*/

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {

        return userService.register(user);
    }

    /***
     * 校验参数
     * @param str
     * @param type
     * @return*/
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {

        return userService.checkValid(str, type);
    }

    /***
     * 获取用户详细信息
     * @param
     * @return*/
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByError("用户未登陆，无法查询用户信息");
    }

    /***
     * 根据用户名获取忘记密码的问题
     * @param username
     * @return*/
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getForgetPasswordQuestion(String username) {
        return userService.getForgetPasswordQuestion(username);
    }

    /***
     * 查看用户是否回答问题正确
     * @param username
     * @param question
     * @param answer
     * @return*/
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return userService.forgetCheckAnswer(username, question, answer);
    }

    /***
     * 根据用户名，密码，token设定新的密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /***
     * 登录状态下的重置密码
     * @param passwordOld
     * @param passwordNew
     * @param
     * @return*/
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew
    , HttpServletRequest request) {
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return userService.resetPassword(passwordOld, passwordNew, user);
        }
        return ServerResponse.createByError("用户未登录");

    }

    /***
     * 更新个人信息
     * @param
     * @param user
     * @return*/
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(User user,HttpServletRequest request,
                                                  HttpServletResponse servletResponse) {



        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User currentUser = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername());
            //开始更新个人信息,注：更新后的用户个人信息重新存入session中
            ServerResponse<User> response = userService.updateInformation(user);
            CookieUtil.writeToken(servletResponse, cookieValue);
            RedisShardPoolUtil.setEx(cookieValue,
                    JsonUtil.object2String(response.getData()), 60 * 30);
            return response;
        }
        return ServerResponse.createByError("用户未登录");
    }


    /***
     * 获取用户个人信息
     * @param
     * @return*/
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request) {
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return userService.getUserInformation(user.getId());
        }
        return ServerResponse.createByError("用户未登录");
    }


}
