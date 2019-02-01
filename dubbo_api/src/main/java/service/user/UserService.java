package service.user;


import entity.User;
import req.UserLoginReq;

/**
 * @Author khe
 * @Date 2019/1/29
 */
public interface UserService {

	User login(UserLoginReq req);


}
