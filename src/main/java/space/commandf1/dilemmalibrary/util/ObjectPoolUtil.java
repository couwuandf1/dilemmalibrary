package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 对象池工具类，用于对象复用以减少GC压力
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高性能
 */
public class ObjectPoolUtil<T> {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(ObjectPoolUtil.class)
    );

    private final ConcurrentLinkedQueue<T> pool;
    private final Supplier<T> factory;
    private final Consumer<T> resetter;
    private final int maxSize;
    private final AtomicInteger createdCount;
    private final AtomicInteger borrowedCount;
    private final AtomicInteger returnedCount;

    /**
     * 构造函数
     *
     * @param factory 对象工厂函数
     * @param resetter 对象重置函数
     * @param maxSize 最大池大小
     */
    public ObjectPoolUtil(@NotNull Supplier<T> factory, @NotNull Consumer<T> resetter, int maxSize) {
        this.pool = new ConcurrentLinkedQueue<>();
        this.factory = factory;
        this.resetter = resetter;
        this.maxSize = maxSize > 0 ? maxSize : 16; // 默认最大池大小为16
        this.createdCount = new AtomicInteger(0);
        this.borrowedCount = new AtomicInteger(0);
        this.returnedCount = new AtomicInteger(0);
    }

    /**
     * 构造函数（无重置函数）
     *
     * @param factory 对象工厂函数
     * @param maxSize 最大池大小
     */
    public ObjectPoolUtil(@NotNull Supplier<T> factory, int maxSize) {
        this(factory, t -> {}, maxSize);
    }

    /**
     * 从对象池中借用对象
     *
     * @return 对象实例
     */
    @NotNull
    public T borrowObject() {
        T object = pool.poll();
        if (object == null) {
            // 池中无对象，创建新对象
            object = factory.get();
            createdCount.incrementAndGet();
        }
        borrowedCount.incrementAndGet();
        return object;
    }

    /**
     * 将对象归还到对象池
     *
     * @param object 对象实例
     */
    public void returnObject(@Nullable T object) {
        if (object == null) {
            return;
        }
        
        // 重置对象状态
        try {
            resetter.accept(object);
        } catch (Exception e) {
            LOGGER.error("对象重置失败", e);
            return; // 重置失败的对象不放回池中
        }
        
        // 如果池未满，将对象放回池中
        if (pool.size() < maxSize) {
            pool.offer(object);
            returnedCount.incrementAndGet();
        }
    }

    /**
     * 获取池中对象数量
     *
     * @return 池中对象数量
     */
    public int getPoolSize() {
        return pool.size();
    }

    /**
     * 获取创建的对象总数
     *
     * @return 创建的对象总数
     */
    public int getCreatedCount() {
        return createdCount.get();
    }

    /**
     * 获取借出的对象总数
     *
     * @return 借出的对象总数
     */
    public int getBorrowedCount() {
        return borrowedCount.get();
    }

    /**
     * 获取归还的对象总数
     *
     * @return 归还的对象总数
     */
    public int getReturnedCount() {
        return returnedCount.get();
    }

    /**
     * 清空对象池
     */
    public void clear() {
        pool.clear();
        createdCount.set(0);
        borrowedCount.set(0);
        returnedCount.set(0);
    }
}