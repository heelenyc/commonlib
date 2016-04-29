package heelenyc.commonlib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yicheng
 * @since 2015年2月28日
 * 
 * @param <K>
 * @param <V>
 */
public class LRUMapCache<K, V> {
    private Map<K, V> cache = new ConcurrentHashMap<K, V>();
    private Map<K, Long> lastUpdateTimeMap = new ConcurrentHashMap<K, Long>();
    private int expires;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * constractor
     * 
     * @param expires
     *            expires in millisecond.
     */
    public LRUMapCache(int expires) {
        this.expires = expires;
        executorService.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                for (K key : cache.keySet()) {
                    get(key);
                }
            }
        }, expires, expires, TimeUnit.MILLISECONDS);
    }

    /**
     * get value from cache.
     * 
     * @param p
     *            param type
     * @return value
     */
    public V get(K p) {
        Long lastUpdateTime = lastUpdateTimeMap.get(p);
        if (lastUpdateTime == null) {
            lastUpdateTime = 0L;
        }

        if (lastUpdateTime + expires <= System.currentTimeMillis()) {
            // 过期了
            cache.remove(p);
            lastUpdateTimeMap.remove(p);
            return null;
        }
        return cache.get(p);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
        lastUpdateTimeMap.clear();
    }

    /**
     * 手动写
     * 
     * @param p
     * @param v
     */
    public void put(K p, V v) {
        // synchronized (cache) {
        cache.put(p, v);
        // }
        // synchronized (lastUpdateTimeMap) {
        lastUpdateTimeMap.put(p, System.currentTimeMillis());
        // }
    }

}
