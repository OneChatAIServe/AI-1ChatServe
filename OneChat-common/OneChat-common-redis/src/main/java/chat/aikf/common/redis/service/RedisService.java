package chat.aikf.common.redis.service;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的Redis工具类
 *
 * @author ruoyi
 **/
@Component
public class RedisService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     */
    public <T> void setCacheObject(final String key, final T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 缓存基本的对象，并设置过期时间
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.expire(timeout, unit);
    }

    /**
     * 获取有效时间
     */
    public long getExpire(final String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.remainTimeToLive();
    }

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.isExists();
    }

    /**
     * 获得缓存的基本对象
     */
    public <T> T getCacheObject(final String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.delete();
    }

    /**
     * 删除集合对象
     */
    public boolean deleteObject(final Collection<String> keys) {
        // Redisson批量删除操作
        RKeys rkeys = redissonClient.getKeys();
        long deletedCount = rkeys.delete(keys.toArray(new String[0]));
        return deletedCount > 0;
    }

    /**
     * 缓存List数据
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        RList<T> list = redissonClient.getList(key);
        list.clear(); // 清空原有数据
        return list.addAll(dataList) ? dataList.size() : 0;
    }

    /**
     * 获得缓存的list对象
     */
    public <T> List<T> getCacheList(final String key) {
        RList<T> list = redissonClient.getList(key);
        return list.readAll();
    }

    /**
     * 缓存Set
     */
    public <T> RSet<T> setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> set = redissonClient.getSet(key);
        set.clear(); // 清空原有数据
        set.addAll(dataSet);
        return set;
    }

    /**
     * 获得缓存的set
     */
    public <T> Set<T> getCacheSet(final String key) {
        RSet<T> set = redissonClient.getSet(key);
        return set.readAll();
    }

    /**
     * 缓存Map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> map = redissonClient.getMap(key);
            map.clear(); // 清空原有数据
            map.putAll(dataMap);
        }
    }

    /**
     * 获得缓存的Map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        RMap<String, T> map = redissonClient.getMap(key);
        return map.readAllMap();
    }

    /**
     * 往Hash中存入数据
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> map = redissonClient.getMap(key);
        map.put(hKey, value);
    }

    /**
     * 获取Hash中的数据
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> map = redissonClient.getMap(key);
        return map.get(hKey);
    }

    /**
     * 获取多个Hash中的数据
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys) {
        RMap<String, T> map = redissonClient.getMap(key);
        Set<String> keySet = new HashSet<>(hKeys); // 转换为Set
        return map.getAll(keySet).values().stream().toList();
    }

    /**
     * 删除Hash中的某条数据
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        RMap<String, ?> map = redissonClient.getMap(key);
        return map.remove(hKey) != null;
    }

    /**
     * 获得缓存的基本对象列表
     */
    public Collection<String> keys(final String pattern) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> iterable = keys.getKeysByPattern(pattern);
        Set<String> set = new HashSet<>();
        for (String key : iterable) {
            set.add(key);
        }
        return set;
    }

    /**
     * Redisson特有的分布式锁功能
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 获取原子长整型
     */
    public RAtomicLong getAtomicLong(String key) {
        return redissonClient.getAtomicLong(key);
    }



    /**
     * 向队列尾部添加一个元素（相当于 RPUSH）
     */
    public <T> void rPushToQueue(final String key, final T value) {
        RDeque<T> deque = redissonClient.getDeque(key);
        deque.addLast(value);
    }

    /**
     * 从队列头部弹出一个元素（相当于 LPOP，原子操作）
     */
    public <T> T lPopFromQueue(final String key, final Class<T> clazz) {
        RDeque<T> deque = redissonClient.getDeque(key);
        return deque.pollFirst(); // 原子操作，线程安全，无则返回 null
    }


    /**
     * 获取指定队列（Deque）的当前元素数量
     */
    public long getQueueSize(final String key) {
        RDeque<Object> deque = redissonClient.getDeque(key);
        return deque.size();
    }


    /**
     * 获取 Hash 的字段数量（对应 Redis HLEN）
     */
    public long getHashSize(final String key) {
        RMap<Object, Object> map = redissonClient.getMap(key);
        return map.size(); // Redisson 底层调用 HLEN，O(1)
    }

    public <T> boolean addToSet(String key, T value) {
        return redissonClient.getSet(key).add(value);
    }

    public <T> boolean removeFromSet(String key, T value) {
        return redissonClient.getSet(key).remove(value);
    }
}