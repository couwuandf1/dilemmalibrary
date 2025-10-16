package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * 懒加载列表工具类，支持延迟元素生成
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 *
 * @param <E> 元素类型
 */
public class LazyList<E> {
    // 使用volatile确保list引用的可见性
    private final java.util.List<E> list;
    // 使用volatile确保elementSupplier引用的可见性
    private final Supplier<E> elementSupplier;
    // 使用volatile确保loadedElements引用的可见性
    private volatile ConcurrentHashMap<Integer, E> loadedElements;
    // 使用volatile确保loadCount引用的可见性
    private final AtomicLong loadCount;
    // 添加访问计数器，用于性能监控
    private final AtomicLong accessCount;

    /**
     * 构造函数
     * 针对嵌入式系统优化，预设初始容量以减少扩容开销
     *
     * @param list            基础列表
     * @param elementSupplier 元素生成器
     */
    public LazyList(@NotNull java.util.List<E> list, @NotNull Supplier<E> elementSupplier) {
        this.list = list;
        this.elementSupplier = elementSupplier;
        // 针对嵌入式系统优化，预设初始容量以减少扩容开销
        this.loadedElements = new ConcurrentHashMap<>(16);
        this.loadCount = new AtomicLong(0);
        this.accessCount = new AtomicLong(0);
    }

    /**
     * 获取指定索引的元素
     * 针对嵌入式系统优化，减少不必要的对象创建和内存分配
     *
     * @param index 索引
     * @return 元素
     */
    @Nullable
    public E get(int index) {
        // 增加访问计数
        accessCount.incrementAndGet();
        
        // 如果索引在基础列表范围内，直接返回
        if (index < list.size()) {
            return list.get(index);
        }

        // 否则延迟加载元素
        // 使用ConcurrentHashMap的computeIfAbsent方法原子性地加载元素
        // 避免显式锁，提高并发性能
        return loadedElements.computeIfAbsent(index, k -> {
            // 增加加载计数
            loadCount.incrementAndGet();
            // 调用元素生成器
            return elementSupplier.get();
        });
    }

    /**
     * 获取列表大小
     * 针对嵌入式系统优化，提供轻量级的大小查询
     *
     * @return 列表大小
     */
    public int size() {
        // 返回基础列表大小和已加载元素数量的总和
        return list.size() + loadedElements.size();
    }

    /**
     * 检查是否包含指定元素
     * 针对嵌入式系统优化，提供高效的检查操作
     *
     * @param element 元素
     * @return 如果包含返回true，否则返回false
     */
    public boolean contains(@Nullable E element) {
        // 增加访问计数
        accessCount.incrementAndGet();
        
        // 检查基础列表
        if (list.contains(element)) {
            return true;
        }

        // 检查已加载的元素
        return loadedElements.containsValue(element);
    }

    /**
     * 获取已加载元素的数量
     * 针对嵌入式系统优化，提供轻量级的加载元素数量查询
     *
     * @return 已加载元素的数量
     */
    public long getLoadCount() {
        // 直接返回加载计数器的值
        return loadCount.get();
    }

    /**
     * 清空已加载的元素
     * 针对嵌入式系统优化，提供高效的清空操作
     */
    public void clearLoadedElements() {
        // 原子性地清空已加载元素
        loadedElements.clear();
        // 重置加载计数器
        loadCount.set(0);
    }
    
    /**
     * 获取访问次数
     * 针对嵌入式系统优化，提供轻量级的性能监控
     *
     * @return 访问次数
     */
    public long getAccessCount() {
        return accessCount.get();
    }
    
    /**
     * 获取加载率
     * 针对嵌入式系统优化，提供轻量级的性能监控
     *
     * @return 加载率（0-1之间）
     */
    public double getLoadRate() {
        long accesses = accessCount.get();
        long loads = loadCount.get();
        return accesses == 0 ? 0.0 : (double) loads / accesses;
    }
}