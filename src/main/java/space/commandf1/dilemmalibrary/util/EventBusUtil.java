package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 高性能事件总线工具类，提供异步事件发布和订阅功能
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class EventBusUtil {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(EventBusUtil.class)
    );

    // 使用ConcurrentHashMap存储事件类型与订阅者的映射关系
    private final Map<Class<?>, CopyOnWriteArrayList<Subscriber>> subscribers = new ConcurrentHashMap<>();
    
    // 使用线程池处理事件分发
    private final ExecutorService eventExecutor;
    
    // 使用读写锁保护注册和注销操作
    private final ReadWriteLock registryLock = new ReentrantReadWriteLock();
    
    // 性能监控计数器
    private final AtomicLong totalEvents = new AtomicLong(0);
    private final AtomicLong processedEvents = new AtomicLong(0);
    private final AtomicLong failedEvents = new AtomicLong(0);
    
    /**
     * 构造函数，创建一个默认的事件总线
     */
    public EventBusUtil() {
        // 针对嵌入式系统优化，使用优化的线程池
        this.eventExecutor = ThreadUtil.createOptimizedFixedThreadPool(
            Math.max(Runtime.getRuntime().availableProcessors(), 4),
            1000
        );
    }
    
    /**
     * 构造函数，创建一个指定线程池大小的事件总线
     *
     * @param threadPoolSize 线程池大小
     * @param queueSize 队列大小
     */
    public EventBusUtil(int threadPoolSize, int queueSize) {
        this.eventExecutor = ThreadUtil.createOptimizedFixedThreadPool(threadPoolSize, queueSize);
    }
    
    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param subscriber 订阅者对象
     * @param methodName 处理方法名
     */
    public void subscribe(@NotNull Class<?> eventType, @NotNull Object subscriber, @NotNull String methodName) {
        try {
            registryLock.writeLock().lock();
            
            // 查找处理方法
            Method handlerMethod = findHandlerMethod(subscriber.getClass(), methodName, eventType);
            if (handlerMethod == null) {
                throw new IllegalArgumentException("未找到匹配的处理方法: " + methodName);
            }
            
            // 设置方法可访问
            handlerMethod.setAccessible(true);
            
            // 创建订阅者对象
            Subscriber sub = new Subscriber(subscriber, handlerMethod);
            
            // 添加到订阅者列表
            subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(sub);
            
            LOGGER.debug("成功订阅事件: " + eventType.getSimpleName() + " -> " + subscriber.getClass().getSimpleName() + "." + methodName);
        } catch (Exception e) {
            LOGGER.error("订阅事件失败: " + eventType.getSimpleName(), e);
            throw new RuntimeException("订阅事件失败", e);
        } finally {
            registryLock.writeLock().unlock();
        }
    }
    
    /**
     * 取消订阅事件
     *
     * @param eventType 事件类型
     * @param subscriber 订阅者对象
     */
    public void unsubscribe(@NotNull Class<?> eventType, @NotNull Object subscriber) {
        try {
            registryLock.writeLock().lock();
            
            List<Subscriber> subs = subscribers.get(eventType);
            if (subs != null) {
                subs.removeIf(s -> s.target == subscriber);
                LOGGER.debug("成功取消订阅事件: " + eventType.getSimpleName() + " -> " + subscriber.getClass().getSimpleName());
            }
        } finally {
            registryLock.writeLock().unlock();
        }
    }
    
    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public void publish(@NotNull Object event) {
        totalEvents.incrementAndGet();
        
        // 获取事件类型
        Class<?> eventType = event.getClass();
        
        // 查找订阅者
        List<Subscriber> subs = subscribers.get(eventType);
        if (subs == null || subs.isEmpty()) {
            LOGGER.debug("没有订阅者处理事件: " + eventType.getSimpleName());
            return;
        }
        
        // 异步处理事件
        eventExecutor.execute(() -> {
            try {
                // 遍历所有订阅者
                for (Subscriber sub : subs) {
                    try {
                        // 调用处理方法
                        sub.handlerMethod.invoke(sub.target, event);
                        processedEvents.incrementAndGet();
                    } catch (Exception e) {
                        failedEvents.incrementAndGet();
                        LOGGER.error("事件处理失败: " + eventType.getSimpleName() + " -> " + 
                                   sub.target.getClass().getSimpleName() + "." + sub.handlerMethod.getName(), e);
                    }
                }
            } catch (Exception e) {
                failedEvents.incrementAndGet();
                LOGGER.error("事件分发失败: " + eventType.getSimpleName(), e);
            }
        });
    }
    
    /**
     * 查找处理方法
     *
     * @param clazz 类
     * @param methodName 方法名
     * @param eventType 事件类型
     * @return 处理方法
     */
    private Method findHandlerMethod(Class<?> clazz, String methodName, Class<?> eventType) {
        try {
            // 先尝试直接查找
            return clazz.getMethod(methodName, eventType);
        } catch (NoSuchMethodException e) {
            // 查找所有方法
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && 
                    method.getParameterCount() == 1 && 
                    method.getParameterTypes()[0].isAssignableFrom(eventType)) {
                    return method;
                }
            }
            return null;
        }
    }
    
    /**
     * 关闭事件总线
     */
    public void shutdown() {
        ThreadUtil.shutdown(eventExecutor);
        LOGGER.info("事件总线已关闭");
    }
    
    /**
     * 立即关闭事件总线
     */
    public void shutdownNow() {
        ThreadUtil.shutdownNow(eventExecutor);
        LOGGER.info("事件总线已立即关闭");
    }
    
    /**
     * 获取总事件数
     */
    public long getTotalEvents() {
        return totalEvents.get();
    }
    
    /**
     * 获取已处理事件数
     */
    public long getProcessedEvents() {
        return processedEvents.get();
    }
    
    /**
     * 获取失败事件数
     */
    public long getFailedEvents() {
        return failedEvents.get();
    }
    
    /**
     * 计算事件处理成功率
     *
     * @return 事件处理成功率（0-1之间）
     */
    public double getSuccessRate() {
        long total = totalEvents.get();
        long processed = processedEvents.get();
        return total == 0 ? 0.0 : (double) processed / total;
    }
    
    /**
     * 订阅者内部类
     */
    private static class Subscriber {
        final Object target;
        final Method handlerMethod;
        
        Subscriber(Object target, Method handlerMethod) {
            this.target = target;
            this.handlerMethod = handlerMethod;
        }
    }
}