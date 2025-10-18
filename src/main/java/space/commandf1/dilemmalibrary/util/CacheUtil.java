package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 高性能缓存工具类，提供多种缓存实现
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class CacheUtil {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(CacheUtil.class)
    );

    /**
     * LRU缓存实现
     * 使用LinkedHashMap实现LRU算法，针对嵌入式系统优化
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class LRUCache<K, V> {
        private final int maxSize;
        private final ConcurrentHashMap<K, Node<K, V>> cache;
        private final Node<K, V> head;
        private final Node<K, V> tail;
        private final AtomicInteger sizeCounter;
        private final AtomicLong hitCount;
        private final AtomicLong missCount;

        /**
         * 构造函数
         *
         * @param maxSize 最大缓存大小
         */
        public LRUCache(int maxSize) {
            this.maxSize = maxSize;
            this.cache = new ConcurrentHashMap<>(16);
            // 初始化双向链表
            this.head = new Node<>(null, null);
            this.tail = new Node<>(null, null);
            this.head.next = this.tail;
            this.tail.prev = this.head;
            this.sizeCounter = new AtomicInteger(0);
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
        }

        /**
         * 获取缓存值
         *
         * @param key 键
         * @return 值或null（如果未找到）
         */
        @Nullable
        public V get(@NotNull K key) {
            Node<K, V> node = cache.get(key);
            if (node != null) {
                // 移动节点到链表头部
                moveToHead(node);
                hitCount.incrementAndGet();
                return node.value;
            } else {
                missCount.incrementAndGet();
            }
            return null;
        }

        /**
         * 放入缓存值
         *
         * @param key   键
         * @param value 值
         */
        public void put(@NotNull K key, @NotNull V value) {
            Node<K, V> node = cache.get(key);
            if (node != null) {
                // 更新值并移动到链表头部
                node.value = value;
                moveToHead(node);
                return;
            }

            // 检查是否需要移除最久未使用的元素
            if (sizeCounter.get() >= maxSize) {
                // 移除链表尾部节点
                Node<K, V> lastNode = tail.prev;
                if (lastNode != head) {
                    removeNode(lastNode);
                    cache.remove(lastNode.key);
                    sizeCounter.decrementAndGet();
                }
            }

            // 添加新节点到链表头部
            Node<K, V> newNode = new Node<>(key, value);
            addToHead(newNode);
            cache.put(key, newNode);
            sizeCounter.incrementAndGet();
        }

        /**
         * 移除缓存值
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            Node<K, V> node = cache.remove(key);
            if (node != null) {
                removeNode(node);
                sizeCounter.decrementAndGet();
                return node.value;
            }
            return null;
        }

        /**
         * 获取缓存大小
         *
         * @return 缓存大小
         */
        public int size() {
            return sizeCounter.get();
        }

        /**
         * 清空缓存
         */
        public void clear() {
            cache.clear();
            // 重置链表
            head.next = tail;
            tail.prev = head;
            sizeCounter.set(0);
            hitCount.set(0);
            missCount.set(0);
        }

        /**
         * 获取命中率
         *
         * @return 命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }

        /**
         * 双向链表节点
         */
        private static class Node<K, V> {
            K key;
            V value;
            Node<K, V> prev;
            Node<K, V> next;

            Node(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }

        /**
         * 将节点添加到链表头部
         */
        private void addToHead(Node<K, V> node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        /**
         * 从链表中移除节点
         */
        private void removeNode(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        /**
         * 将节点移动到链表头部
         */
        private void moveToHead(Node<K, V> node) {
            removeNode(node);
            addToHead(node);
        }
    }

    /**
     * 过期缓存实现
     * 支持自动过期机制，防止内存泄漏
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class ExpiringCache<K, V> {
        private final ConcurrentHashMap<K, ExpiringValue<V>> cache;
        private final ScheduledExecutorService scheduler;
        private final AtomicLong hitCount;
        private final AtomicLong missCount;
        private final AtomicLong cleanupCount;

        /**
         * 构造函数
         */
        public ExpiringCache() {
            this.cache = new ConcurrentHashMap<>();
            this.scheduler = ThreadUtil.createScheduledThreadPool(1);
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
            this.cleanupCount = new AtomicLong(0);
            
            // 启动定期清理任务，每30秒清理一次过期项
            scheduler.scheduleAtFixedRate(this::cleanupExpired, 30, 30, TimeUnit.SECONDS);
        }

        /**
         * 获取缓存值
         *
         * @param key 键
         * @return 值或null（如果未找到或已过期）
         */
        @Nullable
        public V get(@NotNull K key) {
            ExpiringValue<V> expiringValue = cache.get(key);
            if (expiringValue != null && !expiringValue.isExpired()) {
                hitCount.incrementAndGet();
                return expiringValue.getValue();
            } else {
                missCount.incrementAndGet();
                cache.remove(key); // 移除过期的值
                return null;
            }
        }

        /**
         * 放入缓存值
         *
         * @param key     键
         * @param value   值
         * @param timeout 过期时间（毫秒）
         */
        public void put(@NotNull K key, @NotNull V value, long timeout) {
            ExpiringValue<V> expiringValue = new ExpiringValue<>(value, System.currentTimeMillis() + timeout);
            cache.put(key, expiringValue);
        }

        /**
         * 移除缓存值
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            ExpiringValue<V> expiringValue = cache.remove(key);
            return expiringValue != null ? expiringValue.getValue() : null;
        }

        /**
         * 获取缓存大小
         *
         * @return 缓存大小
         */
        public int size() {
            return cache.size();
        }

        /**
         * 清空缓存
         */
        public void clear() {
            cache.clear();
            hitCount.set(0);
            missCount.set(0);
            cleanupCount.set(0);
        }

        /**
         * 关闭缓存
         */
        public void shutdown() {
            ThreadUtil.shutdown(scheduler);
        }

        /**
         * 获取命中率
         *
         * @return 命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }
        
        /**
         * 获取清理次数
         *
         * @return 清理次数
         */
        public long getCleanupCount() {
            return cleanupCount.get();
        }

        /**
         * 清理过期项
         */
        private void cleanupExpired() {
            long count = 0;
            for (Map.Entry<K, ExpiringValue<V>> entry : cache.entrySet()) {
                if (entry.getValue().isExpired()) {
                    cache.remove(entry.getKey());
                    count++;
                }
            }
            if (count > 0) {
                cleanupCount.addAndGet(count);
                LOGGER.debug("清理了 " + count + " 个过期缓存项");
            }
        }

        /**
         * 过期值包装类
         *
         * @param <V> 值类型
         */
        private static class ExpiringValue<V> {
            private final V value;
            private final long expireTime;

            ExpiringValue(V value, long expireTime) {
                this.value = value;
                this.expireTime = expireTime;
            }

            V getValue() {
                return value;
            }

            boolean isExpired() {
                return System.currentTimeMillis() > expireTime;
            }
        }
    }

    /**
     * 计算缓存实现
     * 支持自动加载和缓存穿透防护
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class ComputingCache<K, V> {
        private final ConcurrentHashMap<K, V> cache;
        private final Function<K, V> loader;
        private final AtomicLong hitCount;
        private final AtomicLong missCount;
        private final AtomicLong loadCount;

        /**
         * 构造函数
         *
         * @param loader 加载函数
         */
        public ComputingCache(@NotNull Function<K, V> loader) {
            this.cache = new ConcurrentHashMap<>();
            this.loader = loader;
            this.hitCount = new AtomicLong(0);
            this.missCount = new AtomicLong(0);
            this.loadCount = new AtomicLong(0);
        }

        /**
         * 获取缓存值，如果不存在则自动加载
         *
         * @param key 键
         * @return 值
         */
        @NotNull
        public V get(@NotNull K key) {
            V value = cache.get(key);
            if (value != null) {
                hitCount.incrementAndGet();
                return value;
            } else {
                missCount.incrementAndGet();
                // 使用computeIfAbsent防止并发加载同一键
                return cache.computeIfAbsent(key, k -> {
                    loadCount.incrementAndGet();
                    return loader.apply(k);
                });
            }
        }

        /**
         * 放入缓存值
         *
         * @param key   键
         * @param value 值
         */
        public void put(@NotNull K key, @NotNull V value) {
            cache.put(key, value);
        }

        /**
         * 移除缓存值
         *
         * @param key 键
         * @return 被移除的值或null（如果未找到）
         */
        @Nullable
        public V remove(@NotNull K key) {
            return cache.remove(key);
        }

        /**
         * 获取缓存大小
         *
         * @return 缓存大小
         */
        public int size() {
            return cache.size();
        }

        /**
         * 清空缓存
         */
        public void clear() {
            cache.clear();
            hitCount.set(0);
            missCount.set(0);
            loadCount.set(0);
        }

        /**
         * 获取命中率
         *
         * @return 命中率（0-1之间）
         */
        public double getHitRate() {
            long hits = hitCount.get();
            long misses = missCount.get();
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }

        /**
         * 获取加载次数
         *
         * @return 加载次数
         */
        public long getLoadCount() {
            return loadCount.get();
        }
    }
}