package com.hk.dubbo_controller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.User;
import com.hk.dubbo_common.service.ICartService;
import com.hk.dubbo_common.util.CookieUtil;
import com.hk.dubbo_common.util.JsonUtil;
import com.hk.dubbo_common.util.RedisShardPoolUtil;
import com.hk.dubbo_common.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 何康
 * @date 2018/11/5 21:12
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Reference(version = "1.0.0")
    private ICartService cartService;

    /***
     * 获取购物车列表
     * @param session
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> list(
            HttpServletResponse httpServletResponse,
            HttpServletRequest request,
            HttpSession session){
        httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return cartService.getCartList(user.getId());
        }
        return ServerResponse.createByError("用户未登录");
    }

    /***
     * 新增商品到购物车
     * @param
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> add(HttpServletResponse httpServletResponse,
                                      HttpServletRequest request,
                                      Integer productId, Integer count){
        httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return cartService.addCart(user.getId(),productId,count);
        }
        return ServerResponse.createByError("用户未登录");

    }

    /***
     * 更新购物车的数量
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping(value = "update.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession session, Integer productId, Integer count){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.updateCart(user.getId(),productId,count);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }

    /***
     * 移除购物车的商品
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping(value = "delete.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> delete(HttpSession session, String productIds){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.deleteCart(user.getId(),productIds);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }

    /***
     * 购物车全选
     * @param session
     * @return
     */
    @RequestMapping(value = "select_all.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> select_all(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.checkOrUnCheck(user.getId(),null,Const.Cart.CHECKED);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }
    /***
     * 购物车全反选
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select_all.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> un_select_all(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.checkOrUnCheck(user.getId(),null,Const.Cart.UN_CHECKED);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }
    /***
     * 购物车单选
     * @param session
     * @return
     */
    @RequestMapping(value = "select.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> select(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.checkOrUnCheck(user.getId(),productId,Const.Cart.CHECKED);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }
    /***
     * 购物车单反选
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVO> un_select(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.checkOrUnCheck(user.getId(),productId,Const.Cart.UN_CHECKED);
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }
    /***
     * 获取购物车的商品数量
     * @param session
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> get_cart_product_count(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return cartService.getCartProductCount(user.getId());
        }
        return ServerResponse.createByError("用户未登录,请登录");
    }

}
