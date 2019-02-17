package com.hk.dubbo_common.service;

import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.Category;

import java.util.List;

/**
 * @author 何康
 * @date 2018/10/31 11:24
 */
public interface ICategoryService {

    //添加类目
    ServerResponse addCategory(String categoryName, Integer parenId);

    //修改类目名称
    ServerResponse modiyfCategoryName(Integer categoryId, String categoryName);

    //获取品类子节点(平级)
    ServerResponse<List<Category>> getCategory(Integer categoryId);

    //递归获取品类所有子节点
    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
