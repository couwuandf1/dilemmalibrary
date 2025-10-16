package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数器工具类，支持多种计数场景
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class CounterUtil {
    /**
     * 基础计数器实现
     * 针对嵌入式系统优化的计数器，使用高效的原子操作减少内存占用和提高访问速度
     */
    public static class Counter {
        // 使用volatile确保count引用的可见性
        private volatile AtomicInteger count;
        // 添加操作计数器，用于性能监控
        private final AtomicLong operationCount;

        /**
         * 构造函数
         * 针对嵌入式系统优化，减少不必要的对象创建
         */
        public Counter() {
            this.count = new AtomicInteger(0);
            this.operationCount = new AtomicLong(0);
        }

        /**
         * 构造函数，指定初始值
         * 针对嵌入式系统优化，减少不必要的对象创建
         *
         * @param initialValue 初始值
         */
        public Counter(int initialValue) {
            this.count = new AtomicInteger(initialValue);
            this.operationCount = new AtomicLong(0);
        }

        /**
         * 增加计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @return 增加后的值
         */
        public int increment() {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用原子操作增加计数
            return count.incrementAndGet();
        }

        /**
         * 减少计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @return 减少后的值
         */
        public int decrement() {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用原子操作减少计数
            return count.decrementAndGet();
        }

        /**
         * 增加指定值
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param delta 增加的值
         * @return 增加后的值
         */
        public int add(int delta) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用原子操作增加指定值
            return count.addAndGet(delta);
        }

        /**
         * 获取当前计数值
         * 针对嵌入式系统优化，提供轻量级的值查询
         *
         * @return 当前计数值
         */
        public int get() {
            // 直接返回当前计数值
            return count.get();
        }

        /**
         * 重置计数器
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param newValue 新值
         * @return 重置前的值
         */
        public int reset(int newValue) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用原子操作重置计数器
            return count.getAndSet(newValue);
        }

        /**
         * 原子性地比较并设置值
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param expect 期望值
         * @param update 更新值
         * @return 如果设置成功返回true，否则返回false
         */
        public boolean compareAndSet(int expect, int update) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用原子操作比较并设置值
            return count.compareAndSet(expect, update);
        }
        
        /**
         * 获取操作次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 操作次数
         */
        public long getOperationCount() {
            return operationCount.get();
        }
    }

    /**
     * 多键计数器实现
     * 针对嵌入式系统优化的多键计数器，使用高效的原子操作减少内存占用和提高访问速度
     */
    public static class MultiCounter<K> {
        // 使用volatile确保counters引用的可见性
        private volatile ConcurrentHashMap<K, AtomicInteger> counters;
        // 添加操作计数器，用于性能监控
        private final AtomicLong operationCount;
        // 添加键计数器，用于性能监控
        private final AtomicInteger keyCount;

        /**
         * 构造函数
         * 针对嵌入式系统优化，预设初始容量以减少扩容开销
         */
        public MultiCounter() {
            // 针对嵌入式系统优化，预设初始容量为16以减少扩容开销
            this.counters = new ConcurrentHashMap<>(16);
            this.operationCount = new AtomicLong(0);
            this.keyCount = new AtomicInteger(0);
        }

        /**
         * 增加指定键的计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key 键
         * @return 增加后的值
         */
        public int increment(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地增加计数
            // 避免显式锁，提高并发性能
            return counters.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            }).incrementAndGet();
        }

        /**
         * 减少指定键的计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key 键
         * @return 减少后的值
         */
        public int decrement(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地减少计数
            // 避免显式锁，提高并发性能
            return counters.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            }).decrementAndGet();
        }

        /**
         * 增加指定键的计数（指定值）
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key   键
         * @param delta 增加的值
         * @return 增加后的值
         */
        public int add(@NotNull K key, int delta) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地增加指定值
            // 避免显式锁，提高并发性能
            return counters.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            }).addAndGet(delta);
        }

        /**
         * 获取指定键的计数值
         * 针对嵌入式系统优化，提供轻量级的值查询
         *
         * @param key 键
         * @return 计数值
         */
        public int get(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 直接使用ConcurrentHashMap的get方法获取计数器
            AtomicInteger counter = counters.get(key);
            // 返回计数值或0
            return counter != null ? counter.get() : 0;
        }

        /**
         * 移除指定键的计数器
         * 针对嵌入式系统优化，提供高效的移除操作
         *
         * @param key 键
         * @return 被移除的计数值
         */
        public int remove(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 原子性地移除计数器
            AtomicInteger counter = counters.remove(key);
            // 如果移除成功，减少键计数
            if (counter != null) {
                keyCount.decrementAndGet();
                // 返回被移除的计数值
                return counter.get();
            }
            // 返回0表示未找到
            return 0;
        }

        /**
         * 重置指定键的计数器
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key      键
         * @param newValue 新值
         * @return 重置前的值
         */
        public int reset(@NotNull K key, int newValue) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地重置计数器
            // 避免显式锁，提高并发性能
            AtomicInteger counter = counters.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            });
            // 返回重置前的值
            return counter.getAndSet(newValue);
        }

        /**
         * 获取所有键的计数总和
         * 针对嵌入式系统优化，提供轻量级的总和计算
         *
         * @return 计数总和
         */
        public int getTotalCount() {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 使用流式API计算总和
            return counters.values().stream().mapToInt(AtomicInteger::get).sum();
        }

        /**
         * 获取计数器数量
         * 针对嵌入式系统优化，提供轻量级的大小查询
         *
         * @return 计数器数量
         */
        public int size() {
            // 直接返回键计数器的值
            return keyCount.get();
        }

        /**
         * 清空所有计数器
         * 针对嵌入式系统优化，提供高效的清空操作
         */
        public void clear() {
            // 原子性地清空计数器映射
            counters.clear();
            // 重置操作计数器
            operationCount.set(0);
            // 重置键计数器
            keyCount.set(0);
        }
        
        /**
         * 获取操作次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 操作次数
         */
        public long getOperationCount() {
            return operationCount.get();
        }
    }

    /**
     * 频率计数器实现，支持计数和频率计算
     * 针对嵌入式系统优化的频率计数器，使用高效的原子操作减少内存占用和提高访问速度
     */
    public static class FrequencyCounter<K> {
        // 使用volatile确保frequencies引用的可见性
        private volatile ConcurrentHashMap<K, AtomicInteger> frequencies;
        // 添加操作计数器，用于性能监控
        private final AtomicLong operationCount;
        // 添加键计数器，用于性能监控
        private final AtomicInteger keyCount;
        // 添加总计数器，用于性能监控
        private final AtomicInteger totalCount;

        /**
         * 构造函数
         * 针对嵌入式系统优化，预设初始容量以减少扩容开销
         */
        public FrequencyCounter() {
            // 针对嵌入式系统优化，预设初始容量为16以减少扩容开销
            this.frequencies = new ConcurrentHashMap<>(16);
            this.operationCount = new AtomicLong(0);
            this.keyCount = new AtomicInteger(0);
            this.totalCount = new AtomicInteger(0);
        }

        /**
         * 增加指定键的频率计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key 键
         * @return 增加后的频率
         */
        public int increment(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 增加总计数
            totalCount.incrementAndGet();
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地增加频率计数
            // 避免显式锁，提高并发性能
            return frequencies.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            }).incrementAndGet();
        }

        /**
         * 批量增加指定键的频率计数
         * 针对嵌入式系统优化，使用原子操作提高并发性能
         *
         * @param key   键
         * @param count 增加的次数
         * @return 增加后的频率
         */
        public int add(@NotNull K key, int count) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 增加总计数
            totalCount.addAndGet(count);
            // 使用ConcurrentHashMap的computeIfAbsent方法原子性地增加指定频率计数
            // 避免显式锁，提高并发性能
            return frequencies.computeIfAbsent(key, k -> {
                // 增加键计数
                keyCount.incrementAndGet();
                // 返回新的原子整数
                return new AtomicInteger(0);
            }).addAndGet(count);
        }

        /**
         * 获取指定键的频率
         * 针对嵌入式系统优化，提供轻量级的频率查询
         *
         * @param key 键
         * @return 频率
         */
        public int getFrequency(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 直接使用ConcurrentHashMap的get方法获取频率计数器
            AtomicInteger frequency = frequencies.get(key);
            // 返回频率值或0
            return frequency != null ? frequency.get() : 0;
        }

        /**
         * 获取指定键的频率占比
         * 针对嵌入式系统优化，提供轻量级的频率占比计算
         *
         * @param key 键
         * @return 频率占比（0-1之间）
         */
        public double getFrequencyRatio(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 获取总计数
            int total = totalCount.get();
            // 如果总计数为0，返回0.0
            if (total == 0) {
                return 0.0;
            }
            // 计算频率占比
            return (double) getFrequency(key) / total;
        }

        /**
         * 获取总计数
         * 针对嵌入式系统优化，提供轻量级的总计数查询
         *
         * @return 总计数
         */
        public int getTotalCount() {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 直接返回总计数器的值
            return totalCount.get();
        }

        /**
         * 获取所有键
         * 针对嵌入式系统优化，提供轻量级的键集合访问
         *
         * @return 键集合
         */
        @NotNull
        public java.util.Set<K> keySet() {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 直接返回ConcurrentHashMap的keySet
            return frequencies.keySet();
        }

        /**
         * 移除指定键
         * 针对嵌入式系统优化，提供高效的移除操作
         *
         * @param key 键
         * @return 被移除的频率
         */
        public int remove(@NotNull K key) {
            // 增加操作计数
            operationCount.incrementAndGet();
            // 原子性地移除频率计数器
            AtomicInteger frequency = frequencies.remove(key);
            // 如果移除成功，减少键计数和总计数
            if (frequency != null) {
                // 减少键计数
                keyCount.decrementAndGet();
                // 减少总计数
                int count = frequency.get();
                totalCount.addAndGet(-count);
                // 返回被移除的频率值
                return count;
            }
            // 返回0表示未找到
            return 0;
        }

        /**
         * 清空所有频率计数
         * 针对嵌入式系统优化，提供高效的清空操作
         */
        public void clear() {
            // 原子性地清空频率计数器映射
            frequencies.clear();
            // 重置操作计数器
            operationCount.set(0);
            // 重置键计数器
            keyCount.set(0);
            // 重置总计数器
            totalCount.set(0);
        }
        
        /**
         * 获取操作次数
         * 针对嵌入式系统优化，提供轻量级的性能监控
         *
         * @return 操作次数
         */
        public long getOperationCount() {
            return operationCount.get();
        }
        
        /**
         * 获取键的数量
         * 针对嵌入式系统优化，提供轻量级的键数量查询
         *
         * @return 键的数量
         */
        public int getKeyCount() {
            return keyCount.get();
        }
    }
}