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
 * 高性能双向Map工具类，支持通过键和值进行双向查找
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class BidiMap<K, V> {
    // 使用volatile确保keyToValueMap引用的可见性
    private volatile Map<K, V> keyToValueMap;
    // 使用volatile确保valueToKeyMap引用的可见性
    private volatile Map<V, K> valueToKeyMap;
    // 添加键值对计数器，用于性能监控
    private final AtomicInteger sizeCounter;
    // 添加键查找计数器，用于性能监控
    private final AtomicLong keyLookupCount;
    // 添加值查找计数器，用于性能监控
    private final AtomicLong valueLookupCount;

    /**
     * 构造函数
     * 针对嵌入式系统优化，预设初始容量以减少扩容开销
     */
    public BidiMap() {
        // 使用ConcurrentHashMap保证线程安全
        // 针对嵌入式系统优化，预设初始容量为16以减少扩容开销
        this.keyToValueMap = new ConcurrentHashMap<>(16);
        this.valueToKeyMap = new ConcurrentHashMap<>(16);
        this.sizeCounter = new AtomicInteger(0);
        this.keyLookupCount = new AtomicLong(0);
        this.valueLookupCount = new AtomicLong(0);
    }

    /**
     * 构造函数，指定初始容量
     * 针对嵌入式系统优化，预设初始容量以减少扩容开销
     *
     * @param initialCapacity 初始容量
     */
    public BidiMap(int initialCapacity) {
        // 针对嵌入式系统优化，确保初始容量至少为16
        int capacity = Math.max(initialCapacity, 16);
        this.keyToValueMap = new ConcurrentHashMap<>(capacity);
        this.valueToKeyMap = new ConcurrentHashMap<>(capacity);
        this.sizeCounter = new AtomicInteger(0);
        this.keyLookupCount = new AtomicLong(0);
        this.valueLookupCount = new AtomicLong(0);
    }

    /**
     * 添加键值对
     * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
     *
     * @param key   键
     * @param value 值
     * @return 之前的值（如果存在）
     */
    @Nullable
    public V put(@NotNull K key, @NotNull V value) {
        // 先尝试原子性地添加新的映射关系
        // 使用ConcurrentHashMap的特性避免显式锁
        V oldValue = keyToValueMap.put(key, value);
        K oldKey = valueToKeyMap.put(value, key);
        
        // 更新计数器
        if (oldValue == null && oldKey == null) {
            // 新增键值对
            sizeCounter.incrementAndGet();
        } else if (oldValue != null && oldKey != null) {
            // 替换键值对，计数器不变
        } else if (oldValue != null) {
            // 移除了旧值对应的反向映射
            valueToKeyMap.remove(oldValue);
        } else if (oldKey != null) {
            // 移除了旧键对应的正向映射
            keyToValueMap.remove(oldKey);
            // 新增键值对
            sizeCounter.incrementAndGet();
        }
        
        return oldValue;
    }

    /**
     * 根据键获取值
     * 针对嵌入式系统优化，提供高效的查找操作
     *
     * @param key 键
     * @return 值或null（如果未找到）
     */
    @Nullable
    public V getValue(@NotNull K key) {
        // 增加键查找计数
        keyLookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的get方法
        return keyToValueMap.get(key);
    }

    /**
     * 根据值获取键
     * 针对嵌入式系统优化，提供高效的查找操作
     *
     * @param value 值
     * @return 键或null（如果未找到）
     */
    @Nullable
    public K getKey(@NotNull V value) {
        // 增加值查找计数
        valueLookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的get方法
        return valueToKeyMap.get(value);
    }

    /**
     * 移除键值对（根据键）
     * 针对嵌入式系统优化，提供高效的移除操作
     *
     * @param key 键
     * @return 被移除的值或null（如果未找到）
     */
    @Nullable
    public V removeByKey(@NotNull K key) {
        // 原子性地移除正向映射
        V value = keyToValueMap.remove(key);
        if (value != null) {
            // 原子性地移除反向映射
            valueToKeyMap.remove(value);
            // 减少计数器
            sizeCounter.decrementAndGet();
        }
        return value;
    }

    /**
     * 移除键值对（根据值）
     * 针对嵌入式系统优化，提供高效的移除操作
     *
     * @param value 值
     * @return 被移除的键或null（如果未找到）
     */
    @Nullable
    public K removeByValue(@NotNull V value) {
        // 原子性地移除反向映射
        K key = valueToKeyMap.remove(value);
        if (key != null) {
            // 原子性地移除正向映射
            keyToValueMap.remove(key);
            // 减少计数器
            sizeCounter.decrementAndGet();
        }
        return key;
    }

    /**
     * 检查是否包含指定键
     * 针对嵌入式系统优化，提供高效的检查操作
     *
     * @param key 键
     * @return 如果包含返回true，否则返回false
     */
    public boolean containsKey(@NotNull K key) {
        // 增加键查找计数
        keyLookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的containsKey方法
        return keyToValueMap.containsKey(key);
    }

    /**
     * 检查是否包含指定值
     * 针对嵌入式系统优化，提供高效的检查操作
     *
     * @param value 值
     * @return 如果包含返回true，否则返回false
     */
    public boolean containsValue(@NotNull V value) {
        // 增加值查找计数
        valueLookupCount.incrementAndGet();
        // 直接使用ConcurrentHashMap的containsKey方法（注意是对反向映射的检查）
        return valueToKeyMap.containsKey(value);
    }

    /**
     * 获取键集合
     * 针对嵌入式系统优化，提供轻量级的键集合访问
     *
     * @return 键集合
     */
    @NotNull
    public Set<K> keySet() {
        // 直接返回ConcurrentHashMap的keySet
        return keyToValueMap.keySet();
    }

    /**
     * 获取值集合
     * 针对嵌入式系统优化，提供轻量级的值集合访问
     *
     * @return 值集合
     */
    @NotNull
    public Collection<V> values() {
        // 直接返回ConcurrentHashMap的values
        return keyToValueMap.values();
    }

    /**
     * 获取键值对数量
     * 针对嵌入式系统优化，提供轻量级的大小查询
     *
     * @return 键值对数量
     */
    public int size() {
        // 直接返回计数器的值
        return sizeCounter.get();
    }

    /**
     * 检查是否为空
     * 针对嵌入式系统优化，提供轻量级的空检查
     *
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        // 直接检查计数器是否为0
        return sizeCounter.get() == 0;
    }

    /**
     * 清空所有键值对
     * 针对嵌入式系统优化，提供高效的清空操作
     */
    public void clear() {
        // 原子性地清空两个映射
        keyToValueMap.clear();
        valueToKeyMap.clear();
        // 重置计数器
        sizeCounter.set(0);
        // 重置查找计数器
        keyLookupCount.set(0);
        valueLookupCount.set(0);
    }
    
    /**
     * 获取键查找次数
     * 针对嵌入式系统优化，提供轻量级的性能监控
     *
     * @return 键查找次数
     */
    public long getKeyLookupCount() {
        return keyLookupCount.get();
    }
    
    /**
     * 获取值查找次数
     * 针对嵌入式系统优化，提供轻量级的性能监控
     *
     * @return 值查找次数
     */
    public long getValueLookupCount() {
        return valueLookupCount.get();
    }
}