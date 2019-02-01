package com.hk.dubbo_common.dao;


import com.hk.dubbo_common.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    //获取品类子节点(平级)
    List<Category> getCategory(Integer categoryId);
}