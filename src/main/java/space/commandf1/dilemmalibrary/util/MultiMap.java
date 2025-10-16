package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多值Map工具类，支持一个键对应多个值
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class MultiMap<K, V> {
    // 使用volatile确保map引用的可见性
    private volatile Map<K, Set<V>> map;
    // 添加键值对计数器，用于性能监控
    private final AtomicInteger totalSizeCounter;
    // 添加键计数器，用于性能监控
    private final AtomicInteger keySizeCounter;
    // 添加查找计数器，用于性能监控
    private final AtomicLong lookupCount;

    /**
     * 构造函数
     * 针对嵌入式系统优化，预设初始容量以减少扩容开销
     */
    public MultiMap() {
        // 针对嵌入式系统优化，预设初始容量为16以减少扩容开销
        this.map = new ConcurrentHashMap<>(16);
        this.totalSizeCounter = new AtomicInteger(0);
        this.keySizeCounter = new AtomicInteger(0);
        this.lookupCount = new AtomicLong(0);
    }

    /**
     * 构造函数，指定初始容量
     * 针对嵌入式系统优化，预设初始容量以减少扩容开销
     *
     * @param initialCapacity 初始容量
     */
    public MultiMap(int initialCapacity) {
        // 针对嵌入式系统优化，确保初始容量至少为16
        int capacity = Math.max(initialCapacity, 16);
        this.map = new ConcurrentHashMap<>(capacity);
        this.totalSizeCounter = new AtomicInteger(0);
        this.keySizeCounter = new AtomicInteger(0);
        this.lookupCount = new AtomicLong(0);
    }

    /**
     * 添加键值对
     * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
     *
     * @param key   键
     * @param value 值
     * @return 如果集合发生改变返回true，否则返回false
     */
    public boolean put(@NotNull K key, @NotNull V value) {
        // 使用ConcurrentHashMap的computeIfAbsent方法原子性地添加值
        // 避免显式锁，提高并发性能
        Set<V> values = map.computeIfAbsent(key, k -> {
            // 增加键计数器
            keySizeCounter.incrementAndGet();
            // 返回新的并发安全集合
            return ConcurrentHashMap.newKeySet();
        });
        
        // 添加值到集合中
        boolean changed = values.add(value);
        // 如果添加成功，增加总计数器
        if (changed) {
            totalSizeCounter.incrementAndGet();
        }
        return changed;
    }

    /**
     * 批量添加值到指定键
     * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
     *
     * @param key    键
     * @param values 值集合
     * @return 如果集合发生改变返回true，否则返回false
     */
    public boolean putAll(@NotNull K key, @NotNull Collection<V> values) {
        // 使用ConcurrentHashMap的computeIfAbsent方法原子性地添加值
        // 避免显式锁，提高并发性能
        Set<V> valueSet = map.computeIfAbsent(key, k -> {
            // 增加键计数器
            keySizeCounter.incrementAndGet();
            // 返回新的并发安全集合
            return ConcurrentHashMap.newKeySet();
        });
        
        // 批量添加值到集合中
        boolean changed = valueSet.addAll(values);
        // 如果添加成功，增加总计数器
        if (changed) {
            totalSizeCounter.addAndGet(values.size());
        }
        return changed;
    }

    /**
     * 获取指定键的所有值
     * 针对嵌入式系统优化，提供高效的查找操作
     *
     * @param key 键
     * @return 值集合或空集合（如果未找到）
     */
    @NotNull
    public Set<V> get(@NotNull K key) {
        // 增加查找计数
        lookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的get方法
        Set<V> values = map.get(key);
        // 返回值集合或空集合
        return values != null ? values : ConcurrentHashMap.newKeySet();
    }

    /**
     * 检查是否包含指定键值对
     * 针对嵌入式系统优化，提供高效的检查操作
     *
     * @param key   键
     * @param value 值
     * @return 如果包含返回true，否则返回false
     */
    public boolean contains(@NotNull K key, @NotNull V value) {
        // 增加查找计数
        lookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的get方法获取值集合
        Set<V> values = map.get(key);
        // 检查值集合是否包含指定值
        return values != null && values.contains(value);
    }

    /**
     * 移除指定键值对
     * 针对嵌入式系统优化，提供高效的移除操作
     *
     * @param key   键
     * @param value 值
     * @return 如果集合发生改变返回true，否则返回false
     */
    public boolean remove(@NotNull K key, @NotNull V value) {
        // 直接使用ConcurrentHashMap的get方法获取值集合
        Set<V> values = map.get(key);
        if (values != null) {
            // 从值集合中移除指定值
            boolean removed = values.remove(value);
            // 如果移除成功，减少总计数器
            if (removed) {
                totalSizeCounter.decrementAndGet();
                // 如果该键对应的值集合为空，则移除该键
                if (values.isEmpty()) {
                    // 原子性地移除键
                    if (map.remove(key, values)) {
                        // 减少键计数器
                        keySizeCounter.decrementAndGet();
                    }
                }
            }
            return removed;
        }
        return false;
    }

    /**
     * 移除指定键的所有值
     * 针对嵌入式系统优化，提供高效的移除操作
     *
     * @param key 键
     * @return 被移除的值集合或null（如果未找到）
     */
    @Nullable
    public Set<V> removeAll(@NotNull K key) {
        // 原子性地移除键及其对应的值集合
        Set<V> values = map.remove(key);
        if (values != null) {
            // 减少总计数器
            totalSizeCounter.addAndGet(-values.size());
            // 减少键计数器
            keySizeCounter.decrementAndGet();
        }
        return values;
    }

    /**
     * 获取所有键
     * 针对嵌入式系统优化，提供轻量级的键集合访问
     *
     * @return 键集合
     */
    @NotNull
    public Set<K> keySet() {
        // 直接返回ConcurrentHashMap的keySet
        return map.keySet();
    }

    /**
     * 获取所有值
     * 针对嵌入式系统优化，提供轻量级的值集合访问
     *
     * @return 值集合
     */
    @NotNull
    public Set<V> values() {
        // 创建新的并发安全集合
        Set<V> allValues = ConcurrentHashMap.newKeySet();
        // 遍历所有值集合，合并到结果集合中
        for (Set<V> values : map.values()) {
            allValues.addAll(values);
        }
        return allValues;
    }

    /**
     * 获取键值对总数
     * 针对嵌入式系统优化，提供轻量级的大小查询
     *
     * @return 键值对总数
     */
    public int size() {
        // 直接返回总计数器的值
        return totalSizeCounter.get();
    }

    /**
     * 获取键的数量
     * 针对嵌入式系统优化，提供轻量级的键数量查询
     *
     * @return 键的数量
     */
    public int keySize() {
        // 直接返回键计数器的值
        return keySizeCounter.get();
    }

    /**
     * 检查是否为空
     * 针对嵌入式系统优化，提供轻量级的空检查
     *
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        // 直接检查键计数器是否为0
        return keySizeCounter.get() == 0;
    }

    /**
     * 清空所有键值对
     * 针对嵌入式系统优化，提供高效的清空操作
     */
    public void clear() {
        // 原子性地清空映射
        map.clear();
        // 重置总计数器
        totalSizeCounter.set(0);
        // 重置键计数器
        keySizeCounter.set(0);
        // 重置查找计数器
        lookupCount.set(0);
    }
    
    /**
     * 获取查找次数
     * 针对嵌入式系统优化，提供轻量级的性能监控
     *
     * @return 查找次数
     */
    public long getLookupCount() {
        return lookupCount.get();
    }
}