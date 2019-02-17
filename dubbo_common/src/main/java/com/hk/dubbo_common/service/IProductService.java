package com.hk.dubbo_common.service;

import com.github.pagehelper.PageInfo;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.Product;
import com.hk.dubbo_common.vo.ProductDetailVO;

/**
 * @author 何康
 * @date 2018/11/3 9:52
 */
public interface IProductService {

    //保存或更新商品
    ServerResponse<String> saveOrUpdateProduct(Product product);

    //设置商品的状态
    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    //获取商品的详细信息
    ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);

    //获取商品列表
    ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize);

    //获取搜索的商品
    ServerResponse<PageInfo> searchProduct(Integer pageNum, Integer pageSize, Integer productId, String productName);

    //获取前端搜索的商品
    ServerResponse<PageInfo> getProductByCategoryIdAndKeyword(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);

    //前端获取单个商品详情
    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);
}
