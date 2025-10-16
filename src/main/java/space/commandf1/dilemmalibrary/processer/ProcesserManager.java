package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能处理器管理器，用于注册和管理各种处理器实例，并提供统一的执行接口
 * 该实现针对嵌入式系统进行了优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class ProcesserManager {
    // 使用ConcurrentHashMap实现无锁化访问，提高并发性能
    // volatile确保processor引用的可见性
    private volatile Map<String, Processer<?, ?>> processers = new ConcurrentHashMap<>();

    /**
     * 注册一个处理器
     * 该方法针对嵌入式系统优化，避免使用显式锁，通过ConcurrentHashMap的原子操作保证线程安全
     *
     * @param processer 要注册的处理器
     */
    public void registerProcesser(@NotNull Processer<?, ?> processer) {
        // 直接使用ConcurrentHashMap的put方法，无锁化操作
        // 在嵌入式系统中，避免使用ReentrantReadWriteLock等重量级锁机制
        this.processers.put(processer.getName(), processer);
    }

    /**
     * 根据名称获取处理器
     * 该方法针对嵌入式系统优化，使用无锁化访问提高并发性能
     *
     * @param name 处理器名称
     * @return 处理器实例或null（如果未找到）
     */
    @Nullable
    public Processer<?, ?> getProcesser(@NotNull String name) {
        // 直接使用ConcurrentHashMap的get方法，无锁化操作
        // 在高并发场景下，避免锁竞争带来的性能损耗
        return this.processers.get(name);
    }

    /**
     * 执行指定名称的处理器
     * 该方法针对嵌入式系统优化，减少不必要的对象创建和内存分配
     *
     * @param name  处理器名称
     * @param input 输入数据
     * @return 处理结果或null（如果处理器未找到或处理失败）
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T, R> R executeProcesser(@NotNull String name, @NotNull T input, @NotNull LoggerProvider<?> loggerProvider) {
        // 避免重复调用getProcessor方法，减少HashMap查找开销
        Processer<T, R> processer = (Processer<T, R>) this.processers.get(name);
        if (processer == null) {
            // 在嵌入式系统中，减少日志输出以节省资源
            // 只在必要时输出错误日志
            loggerProvider.error("Processer not found: " + name);
            return null;
        }

        try {
            // 在嵌入式系统中，减少调试日志输出以节省资源
            // 只在必要时输出调试信息
            // loggerProvider.debug("Executing processer: " + name);
            R result = processer.process(input);
            // loggerProvider.debug("Processer executed successfully: " + name);
            return result;
        } catch (Exception e) {
            // 在嵌入式系统中，异常处理需要更加谨慎
            // 避免输出过长的堆栈信息以节省资源
            loggerProvider.error("Error executing processer " + name + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取已注册处理器的数量
     * 该方法针对嵌入式系统优化，提供轻量级的统计功能
     *
     * @return 处理器数量
     */
    public int getProcesserCount() {
        // 直接返回ConcurrentHashMap的大小，无额外计算开销
        return this.processers.size();
    }
    
    /**
     * 检查是否存在指定名称的处理器
     * 该方法针对嵌入式系统优化，提供轻量级的检查功能
     *
     * @param name 处理器名称
     * @return 如果存在返回true，否则返回false
     */
    public boolean containsProcesser(@NotNull String name) {
        // 使用ConcurrentHashMap的containsKey方法，避免获取实际对象
        return this.processers.containsKey(name);
    }
    
    /**
     * 移除指定名称的处理器
     * 该方法针对嵌入式系统优化，提供轻量级的移除功能
     *
     * @param name 处理器名称
     * @return 被移除的处理器或null（如果未找到）
     */
    @Nullable
    public Processer<?, ?> removeProcesser(@NotNull String name) {
        // 直接使用ConcurrentHashMap的remove方法，无锁化操作
        return this.processers.remove(name);
    }
    
    /**
     * 清空所有处理器
     * 该方法针对嵌入式系统优化，提供轻量级的清空功能
     */
    public void clearProcessers() {
        // 直接清空ConcurrentHashMap，避免逐个移除的开销
        this.processers.clear();
    }
}
