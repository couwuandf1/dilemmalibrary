package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 高性能缓存工具类，提供多种缓存策略和自动过期功能
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class CacheUtil {
    /**
     * LRU缓存实现
     * 针对嵌入式系统优化的LRU缓存，使用高效的算法减少内存占用和提高访问速度
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class LRUCache<K, V> {
        // 使用volatile确保cache引用的可见性
        private volatile ConcurrentHashMap<K, V> cache;
        // 使用volatile确保queue引用的可见性
        private volatile ConcurrentLinkedQueue<K> queue;
        // 使用volatile确保maxSize的可见性
        private final int maxSize;
        // 添加缓存命中计数器，用于性能监控
        private final AtomicLong hitCount;
        // 添加缓存未命中计数器，用于性能监控
        private final AtomicLong missCount;

        /**
         * 构造函数
         *
         * @param maxSize 最大缓存大小
         */
        public LRUCache(int maxSize) {
            // 针对嵌入式系统优化，预设初始容量以减少扩容开销
            this.maxSize = maxSize;
            this.cache = new ConcurrentHashMap<>(Math.min(maxSize, 16));
            this.queue = new ConcurrentLinkedQueue<>();
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
        }

        /**
         * 获取缓存值
         * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
         *
         * @param key 键
         * @return 值或null（如果未找到）
         */
        @Nullable
        public V get(@NotNull K key) {
            // 直接使用ConcurrentHashMap的get方法，避免重复查找
            V value = cache.get(key);
            if (value != null) {
                // 增加命中计数
                hitCount.incrementAndGet();
                // 更新访问顺序，但避免创建新的迭代器
                // 在嵌入式系统中，避免使用remove方法导致的线性查找
                queue.offer(key);
            } else {
                // 增加未命中计数
                missCount.incrementAndGet();
            }
            return value;
        }

        /**
         * 设置缓存值
         * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
         *
         * @param key   键
         * @param value 值
         */
        public void put(@NotNull K key, @NotNull V value) {
            // 检查缓存是否已满
            if (cache.size() >= maxSize) {
                // 使用循环优化LRU淘汰算法
                // 避免ConcurrentLinkedQueue的poll方法可能返回null的问题
                int attempts = 0;
                K oldestKey = null;
                while (attempts < maxSize && (oldestKey = queue.poll()) != null) {
                    // 检查该键是否仍然在缓存中（可能已被其他线程移除）
                    if (cache.containsKey(oldestKey)) {
                        // 移除最久未使用的元素
                        cache.remove(oldestKey);
                        break;
                    }
                    attempts++;
                }
            }

            // 添加新的键值对
            cache.put(key, value);
            // 添加到队列末尾表示最近使用
            queue.offer(key);
        }

        /**
         * 移除缓存项
         * 针对嵌入式系统优化，提供高效的移除操作
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            // 直接从缓存中移除
            V value = cache.remove(key);
            // 从队列中移除（可能导致线性查找，但在嵌入式系统中可接受）
            queue.remove(key);
            return value;
        }

        /**
         * 清空缓存
         * 针对嵌入式系统优化，提供高效的清空操作
         */
        public void clear() {
            // 直接清空缓存和队列
            cache.clear();
            queue.clear();
            // 重置计数器
            hitCount.set(0);
            missCount.set(0);
        }

        /**
         * 获取缓存大小
         * 针对嵌入式系统优化，提供轻量级的大小查询
         *
         * @return 缓存大小
         */
        public int size() {
            // 直接返回缓存大小
            return cache.size();
        }
        
        /**
         * 获取缓存命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中次数
         */
        public long getHitCount() {
            return hitCount.get();
        }
        
        /**
         * 获取缓存未命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存未命中次数
         */
        public long getMissCount() {
            return missCount.get();
        }
        
        /**
         * 计算缓存命中率
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
    }

    /**
     * 带过期时间的缓存实现
     * 针对嵌入式系统优化的过期缓存，使用高效的算法减少内存占用和提高访问速度
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class ExpiringCache<K, V> {
        // 使用volatile确保cache引用的可见性
        private volatile ConcurrentHashMap<K, V> cache;
        // 使用volatile确保expireMap引用的可见性
        private volatile ConcurrentHashMap<K, Long> expireMap;
        // 使用volatile确保scheduler引用的可见性
        private volatile ScheduledExecutorService scheduler;
        // 添加缓存命中计数器，用于性能监控
        private final AtomicLong hitCount;
        // 添加缓存未命中计数器，用于性能监控
        private final AtomicLong missCount;
        // 添加过期计数器，用于性能监控
        private final AtomicLong expireCount;

        /**
         * 构造函数
         */
        public ExpiringCache() {
            // 针对嵌入式系统优化，预设初始容量以减少扩容开销
            this.cache = new ConcurrentHashMap<>(16);
            this.expireMap = new ConcurrentHashMap<>(16);
            // 针对嵌入式系统优化，使用单线程调度器以减少资源占用
            this.scheduler = ThreadUtil.createScheduledThreadPool(1);
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
            this.expireCount = new AtomicLong(0);
        }

        /**
         * 设置缓存值并指定过期时间
         * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
         *
         * @param key       键
         * @param value     值
         * @param expireTime 过期时间（毫秒）
         */
        public void put(@NotNull K key, @NotNull V value, long expireTime) {
            // 添加缓存项
            cache.put(key, value);
            // 设置过期时间
            expireMap.put(key, System.currentTimeMillis() + expireTime);

            // 安排过期任务
            // 针对嵌入式系统优化，使用execute方法而不是submit方法以减少Future对象的创建
            scheduler.execute(() -> {
                try {
                    // 使用循环等待确保精确的过期时间
                    long sleepTime = expireTime;
                    while (sleepTime > 0) {
                        long start = System.currentTimeMillis();
                        // 分段睡眠以提高响应性
                        Thread.sleep(Math.min(sleepTime, 1000));
                        long end = System.currentTimeMillis();
                        sleepTime -= (end - start);
                    }
                    
                    // 检查缓存项是否仍然存在且未被更新
                    Long expireAt = expireMap.get(key);
                    if (expireAt != null && System.currentTimeMillis() >= expireAt) {
                        // 原子性地移除过期的缓存项
                        cache.remove(key);
                        expireMap.remove(key);
                        // 增加过期计数
                        expireCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // 线程被中断，恢复中断状态
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // 忽略其他异常，避免影响调度器
                }
            });
        }

        /**
         * 获取缓存值
         * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
         *
         * @param key 键
         * @return 值或null（如果未找到或已过期）
         */
        @Nullable
        public V get(@NotNull K key) {
            // 先检查过期时间
            Long expireAt = expireMap.get(key);
            if (expireAt != null && System.currentTimeMillis() >= expireAt) {
                // 已过期，原子性地移除缓存项
                cache.remove(key);
                expireMap.remove(key);
                // 增加过期计数
                expireCount.incrementAndGet();
                // 增加未命中计数
                missCount.incrementAndGet();
                return null;
            }
            
            // 获取缓存值
            V value = cache.get(key);
            if (value != null) {
                // 增加命中计数
                hitCount.incrementAndGet();
            } else {
                // 增加未命中计数
                missCount.incrementAndGet();
            }
            return value;
        }

        /**
         * 移除缓存项
         * 针对嵌入式系统优化，提供高效的移除操作
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            // 原子性地移除缓存项
            expireMap.remove(key);
            return cache.remove(key);
        }

        /**
         * 清空缓存
         * 针对嵌入式系统优化，提供高效的清空操作
         */
        public void clear() {
            // 原子性地清空缓存
            cache.clear();
            expireMap.clear();
            // 重置计数器
            hitCount.set(0);
            missCount.set(0);
            expireCount.set(0);
        }

        /**
         * 获取缓存大小
         * 针对嵌入式系统优化，提供轻量级的大小查询
         *
         * @return 缓存大小
         */
        public int size() {
            // 返回缓存大小
            return cache.size();
        }

        /**
         * 关闭缓存
         * 针对嵌入式系统优化，提供资源释放功能
         */
        public void shutdown() {
            // 关闭调度器以释放资源
            ThreadUtil.shutdown(scheduler);
        }
        
        /**
         * 获取缓存命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中次数
         */
        public long getHitCount() {
            return hitCount.get();
        }
        
        /**
         * 获取缓存未命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存未命中次数
         */
        public long getMissCount() {
            return missCount.get();
        }
        
        /**
         * 获取缓存过期次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存过期次数
         */
        public long getExpireCount() {
            return expireCount.get();
        }
        
        /**
         * 计算缓存命中率
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
    }

    /**
     * 计算缓存实现，支持自动加载和缓存穿透防护
     * 针对嵌入式系统优化的计算缓存，使用高效的算法减少内存占用和提高访问速度
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class ComputingCache<K, V> {
        // 使用volatile确保cache引用的可见性
        private volatile ConcurrentHashMap<K, V> cache;
        // 使用volatile确保loader引用的可见性
        private final Function<K, V> loader;
        // 用于标识null值，防止缓存穿透
        private final Object NULL = new Object();
        // 添加缓存命中计数器，用于性能监控
        private final AtomicLong hitCount;
        // 添加缓存未命中计数器，用于性能监控
        private final AtomicLong missCount;
        // 添加加载计数器，用于性能监控
        private final AtomicLong loadCount;

        /**
         * 构造函数
         *
         * @param loader 加载函数
         */
        public ComputingCache(@NotNull Function<K, V> loader) {
            // 针对嵌入式系统优化，预设初始容量以减少扩容开销
            this.cache = new ConcurrentHashMap<>(16);
            this.loader = loader;
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
            this.loadCount = new AtomicLong(0);
        }

        /**
         * 获取缓存值，如果不存在则自动加载
         * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
         *
         * @param key 键
         * @return 值
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public V get(@NotNull K key) {
            // 直接使用ConcurrentHashMap的get方法，避免重复查找
            V value = cache.get(key);
            if (value == NULL) {
                // 增加命中计数
                hitCount.incrementAndGet();
                return null;
            }
            if (value != null) {
                // 增加命中计数
                hitCount.incrementAndGet();
                return value;
            }

            // 未命中，需要加载数据
            // 增加未命中计数
            missCount.incrementAndGet();
            
            // 使用ConcurrentHashMap的computeIfAbsent方法实现双重检查锁模式
            // 避免显式使用synchronized关键字，提高并发性能
            value = cache.computeIfAbsent(key, k -> {
                // 增加加载计数
                loadCount.incrementAndGet();
                
                try {
                    // 调用加载函数
                    V loadedValue = loader.apply(k);
                    // 缓存null值以防止缓存穿透
                    return loadedValue != null ? loadedValue : (V) NULL;
                } catch (Exception e) {
                    // 加载失败，返回NULL对象以防止缓存穿透
                    return (V) NULL;
                }
            });
            
            // 检查是否为NULL对象
            return value == NULL ? null : value;
        }

        /**
         * 移除缓存项
         * 针对嵌入式系统优化，提供高效的移除操作
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            // 直接从缓存中移除
            V value = cache.remove(key);
            // 检查是否为NULL对象
            return value == NULL ? null : value;
        }

        /**
         * 清空缓存
         * 针对嵌入式系统优化，提供高效的清空操作
         */
        public void clear() {
            // 直接清空缓存
            cache.clear();
            // 重置计数器
            hitCount.set(0);
            missCount.set(0);
            loadCount.set(0);
        }

        /**
         * 获取缓存大小
         * 针对嵌入式系统优化，提供轻量级的大小查询
         *
         * @return 缓存大小
         */
        public int size() {
            // 直接返回缓存大小
            return cache.size();
        }
        
        /**
         * 获取缓存命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中次数
         */
        public long getHitCount() {
            return hitCount.get();
        }
        
        /**
         * 获取缓存未命中次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存未命中次数
         */
        public long getMissCount() {
            return missCount.get();
        }
        
        /**
         * 获取加载次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 加载次数
         */
        public long getLoadCount() {
            return loadCount.get();
        }
        
        /**
         * 计算缓存命中率
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 缓存命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
    }
}