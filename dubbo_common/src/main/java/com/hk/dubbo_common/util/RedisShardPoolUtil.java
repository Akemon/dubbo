package com.hk.dubbo_common.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * @author 何康
 * @date 2019/1/12 11:19
 */
@Slf4j
public class RedisShardPoolUtil {

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} error",key,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    //exTime的单位是秒
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error",key,value,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    public static String getSet(String key,String value){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.getSet(key,value);
        } catch (Exception e) {
            log.error("getset key:{} value:{} error",key,value,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

    public static Long setnx(String key,String value){
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardPool.getJedis();
            result = jedis.setnx(key,value);
        } catch (Exception e) {
            log.error("setnx key:{} value:{} error",key,value,e);
            RedisShardPool.returnBrokenJedi(jedis);
            return result;
        }
        RedisShardPool.returnJedis(jedis);
        return result;
    }

}
