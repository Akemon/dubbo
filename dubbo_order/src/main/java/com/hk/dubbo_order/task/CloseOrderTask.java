package com.hk.dubbo_order.task;

import com.hk.dubbo_common.common.Const;
import com.hk.dubbo_common.common.PropertiesUtil;
import com.hk.dubbo_common.service.IOrderService;
import com.hk.dubbo_common.util.RedisShardPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by geely
 */
@Component
@Slf4j
@EnableScheduling
@Configurable
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;


    /***
     * 每分钟执行一次
     */
//    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV3(){
        log.info("关闭订单定时任务启动");
        // 获取锁住的时长
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        // 尝试去获取锁，并设置锁住的时间
        Long setnxResult = RedisShardPoolUtil.setnx(Const.RedisLock.CLOSE_ORDER_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        // 获取锁成功后开始执行关闭订单的业务
        if(setnxResult != null && setnxResult.intValue() == 1){
            closeOrder(Const.RedisLock.CLOSE_ORDER_LOCK);
        }else{
            // 未获取到锁，继续判断，判断时间戳，看是否可以重置并获取到锁，获取到锁的当前时间
            String lockValueStr = RedisShardPoolUtil.get(Const.RedisLock.CLOSE_ORDER_LOCK);
           // 锁不为空并判断锁已经过了释放时间了，即可以获取锁
            if(lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
                // 重新设置锁的过期时间
                String getSetResult = RedisShardPoolUtil.getSet(Const.RedisLock.CLOSE_ORDER_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                // 需要判断之间获取锁的时间和当前重新设置锁时的时间是否相同，防止在设置锁时被其它线程修改
                if(getSetResult == null || (getSetResult != null && StringUtils.equals(lockValueStr,getSetResult))){
                    //获取到锁
                    closeOrder(Const.RedisLock.CLOSE_ORDER_LOCK);
                }else{
                    log.info("没有获取到分布式锁:{}",Const.RedisLock.CLOSE_ORDER_LOCK);
                }
            }else{
                log.info("没有获取到分布式锁:{}",Const.RedisLock.CLOSE_ORDER_LOCK);
            }
        }
        log.info("关闭订单定时任务结束");
    }

    /***
     * 关闭订单
     * @param lockName
     */
    private void closeOrder(String lockName){
        //5秒内释放锁，让其它线程进来，防止死锁
        RedisShardPoolUtil.expire(lockName,5);
        log.info("获取{},ThreadName:{}",Const.RedisLock.CLOSE_ORDER_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","1"));
        iOrderService.closeOrder(hour);
        RedisShardPoolUtil.del(Const.RedisLock.CLOSE_ORDER_LOCK);
        log.info("释放{},ThreadName:{}",Const.RedisLock.CLOSE_ORDER_LOCK,Thread.currentThread().getName());
        log.info("===============================");
    }




}
