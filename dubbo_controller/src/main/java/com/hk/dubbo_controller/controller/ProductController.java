package com.hk.dubbo_controller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.User;
import com.hk.dubbo_common.service.IProductService;
import com.hk.dubbo_common.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author 何康
 * @date 2018/11/5 15:19
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Reference(version = "1.0.0")
    private IProductService productService;

    /***
     * 获取商品搜索列表
     * @param session
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                 Integer categoryId,
                                 String keyword,
                                 @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                 @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                 @RequestParam(value = "orderBy",defaultValue = "")String orderBy){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return productService.getProductByCategoryIdAndKeyword(categoryId,keyword,pageNum,pageSize,orderBy);
        }
        return ServerResponse.createByError("用户未登陆，无法查询产品");
    }

    /***
     * 获取单个商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!= null){
            return productService.getProductDetail(productId);
        }
        return ServerResponse.createByError("用户未登陆，无法查询产品");
    }
}
