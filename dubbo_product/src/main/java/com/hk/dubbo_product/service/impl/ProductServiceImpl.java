package com.hk.dubbo_product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.PropertiesUtil;
import com.hk.dubbo_common.common.ResponseCode;
import com.hk.dubbo_common.common.ServerResponse;
import com.hk.dubbo_common.pojo.OrderItem;
import com.hk.dubbo_product.dao.CategoryMapper;
import com.hk.dubbo_product.dao.ProductMapper;
import com.hk.dubbo_common.pojo.Category;
import com.hk.dubbo_common.pojo.Product;
import com.hk.dubbo_common.service.ICategoryService;
import com.hk.dubbo_common.service.IProductService;
import com.hk.dubbo_common.util.DateTimeUtil;
import com.hk.dubbo_common.vo.ProductDetailVO;
import com.hk.dubbo_common.vo.ProductListVO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 何康
 * @date 2018/11/3 17:55
 */
@Service
@com.alibaba.dubbo.config.annotation.Service(version = "1.0.0")
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    /***
     * 新增或更新产品
     * @param product
     * @return
     */
    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {
        //将产品的主图进行填充
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String images[] = product.getSubImages().split(",");
                if (images.length > 0) product.setMainImage(images[0]);
            }
        }
        //判断是更新还是新增
        if (product.getId() == null) {
            //新增
            product.setCreateTime(new Date());
            int count = productMapper.insert(product);
            if (count > 0) return ServerResponse.createBySuccess("新增产品成功");
            return ServerResponse.createBySuccess("新增产品失败");
        } else {
            //更新
            int count = productMapper.updateByPrimaryKey(product);
            if (count > 0) return ServerResponse.createBySuccess("更新产品成功");
            return ServerResponse.createBySuccess("更新产品失败");
        }

    }

    /***
     * 修改商品销售状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if(count>0) return ServerResponse.createBySuccess("修改销售状态成功");
        return ServerResponse.createByError("修改销售状态失败");
    }

    /***
     * 获取商品的详细信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if(productId == null)return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) return ServerResponse.createByError("商品不存在");
        //返回VO对象
        return ServerResponse.createBySuccess(assembleProductDetailVO(product));
    }

    /****
     * 获取商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //获取商品列表
        List<Product>  productList = productMapper.getAllProduct();
        //封装成商品列表vo对象
        List<ProductListVO> productListVOList = Lists.newArrayList();
        //需要填充初始数据
        PageInfo pageInfo = new PageInfo(productList);
        if(productList == null || productList.size() ==0) {
            return ServerResponse.createByError("商品列表为空");
        }else{
            //将product封装到productListVo
            for(Product product:productList){
                ProductListVO productListVO = new ProductListVO();
                BeanUtils.copyProperties(product,productListVO);
                productListVOList.add(productListVO);
            }
            //修改pageInfo里的数据
            pageInfo.setList(productListVOList);
        }
        return ServerResponse.createBySuccess(pageInfo);
    }

    /****
     * 获取搜索的商品列表
     * @param pageNum
     * @param pageSize
     * @param productId
     * @param productName
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(Integer pageNum, Integer pageSize, Integer productId, String productName) {
        PageHelper.startPage(pageNum,pageSize);
        //将产品名称前后加个%
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        //获取商品列表
        List<Product>  productList = productMapper.getSearchProduct(productId,productName);
        //封装成商品列表vo对象
        List<ProductListVO> productListVOList = Lists.newArrayList();
        //需要填充初始数据
        PageInfo pageInfo = new PageInfo(productList);
        if(productList == null || productList.size() ==0) {
            return ServerResponse.createByError("商品列表为空");
        }else{
            //将product封装到productListVo
            for(Product product:productList){
                ProductListVO productListVO = new ProductListVO();
                BeanUtils.copyProperties(product,productListVO);
                //这里直接将状态置空
                productListVO.setStatus(null);
                productListVOList.add(productListVO);
            }
            //修改pageInfo里的数据
            pageInfo.setList(productListVOList);
        }
        return ServerResponse.createBySuccess(pageInfo);
    }

    /***
     * 获取前端搜索的商品
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByCategoryIdAndKeyword(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
       //判断参数
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        //是否有此分类
        if(categoryId != null){
            Category category =categoryMapper.selectByPrimaryKey(categoryId);
            //无此分类，返回空结果集
            if(category == null) {
                PageHelper.startPage(pageNum,pageSize);
                PageInfo pageInfo = new PageInfo(Lists.newArrayList());
                return ServerResponse.createBySuccess(pageInfo);
            }else{
                //有分类时查找所有父分类
                categoryIdList = (List<Integer>) selectCategoryAndChildrenById(category.getId()).getData();
            }
        }
        //对查询关键字的处理
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //排序处理
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            String[] orderByArray = orderBy.split("_");
            PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
        }
        //开始查询结果集
        List<Product> productList = productMapper.selectByKeywordAndCategoryIds(keyword,categoryIdList);
        PageInfo pageInfo = new PageInfo(productList);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product product:productList){
            ProductListVO productListVO = new ProductListVO();
            BeanUtils.copyProperties(product,productListVO);
            productListVOList.add(productListVO);
        }
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if(productId == null)return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) return ServerResponse.createByError("商品不存在");
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SELL.getCode())
            return ServerResponse.createByError("商品已下架");
        //返回VO对象
        return ServerResponse.createBySuccess(assembleProductDetailVO(product));
    }

    /***
     * 装配productDetailVO对象
     * 主要是对图片服务器的域名设置以及父类目id
     * @param product
     * @return
     */
    public ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product,productDetailVO);
        //设置图片服务器的解析域名前缀
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.hk.com/"));
        //设置当前商品所在类目的父类目id
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        //没有找到类目，只能说明当前类目的最大的类目，父类目id为0
        if(category == null) productDetailVO.setParentCategoryId(0);
        else productDetailVO.setParentCategoryId(category.getParentId());
        //时间转换
        productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVO;
    }


    /***
     * orderService的查找类目
     */

    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet,categoryId);
        //生成categoryId返回
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category category:categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归获取所有子节点
    private Set<Category> findChildrenCategory(Set<Category> categorySet
            ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //获取以该节点作为父节点的所有子节点
        List<Category> categoryList = categoryMapper.getCategory(categoryId);
        //递归调用
        for(Category categoryTemp:categoryList){
            findChildrenCategory(categorySet,categoryTemp.getId());
        }
        return categorySet;

    }


    //商品库存减少
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "product-queue", durable = "true"),
                    exchange = @Exchange(value = "order-exchange", durable = "true", type = "topic"),
                    key = "orderItem"
            )
    )
    @RabbitHandler
    public void onOrderMessage(@Payload List<OrderItem> orderItemList, @Headers Map<String, Object> headers,
                               Channel channel) {
        try {
            System.out.println("------商品模块收到消息，开始消费---------");
            for(OrderItem orderItem :orderItemList){
                Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                product.setStock(product.getStock()-orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("消费者消费信息时异常：{}", e);
        }
    }

}
