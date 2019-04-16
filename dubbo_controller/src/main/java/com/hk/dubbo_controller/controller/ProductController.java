package com.hk.dubbo_controller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.User;
import com.hk.dubbo_common.service.IProductService;
import com.hk.dubbo_common.util.CookieUtil;
import com.hk.dubbo_common.util.JsonUtil;
import com.hk.dubbo_common.util.RedisShardPoolUtil;
import com.hk.dubbo_common.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(Integer categoryId,
                                  String keyword,
                                  HttpServletRequest request,
                                  HttpServletResponse httpServletResponse,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        String cookieValue = CookieUtil.readToken(request);
        ServerResponse<PageInfo> result = JsonUtil.string2Object(RedisShardPoolUtil.get(keyword), ServerResponse.class);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            if (result != null) {
                return result;
            }
            ServerResponse<PageInfo> pageInfoServerResponse = productService.
                    manageProductList(pageNum, pageSize);
            if (keyword != null) {
                RedisShardPoolUtil.setEx(keyword, JsonUtil.object2String(pageInfoServerResponse), 60 * 30);
            }
            return pageInfoServerResponse;
        }
        return ServerResponse.createByError("用户未登录");
    }

    /***
     * 获取单个商品详情
     * @param
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpServletRequest request, Integer productId) {
        String cookieValue = CookieUtil.readToken(request);
        if (cookieValue != null) {
            User user = JsonUtil.string2Object(RedisShardPoolUtil.get(cookieValue), User.class);
            return productService.getProductDetail(productId);
        }
        return ServerResponse.createByError("用户未登录");
    }

}
