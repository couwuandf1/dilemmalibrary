package space.commandf1.dilemmalibrary.util;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高效的任务调度器，提供灵活的任务调度功能
 */
public class TaskScheduler {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(TaskScheduler.class)
    );

    private final ScheduledExecutorService scheduler;
    private final AtomicLong totalTasks = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong failedTasks = new AtomicLong(0);

    /**
     * 构造函数，创建一个默认的调度器
     */
    public TaskScheduler() {
        // 使用优化的线程池创建方法
        this.scheduler = ThreadUtil.createScheduledThreadPool(
            Math.max(Runtime.getRuntime().availableProcessors(), 4)
        );
    }

    /**
     * 构造函数，创建一个指定核心线程数的调度器
     *
     * @param corePoolSize 核心线程数
     */
    public TaskScheduler(int corePoolSize) {
        this.scheduler = ThreadUtil.createScheduledThreadPool(
            Math.max(corePoolSize, Runtime.getRuntime().availableProcessors())
        );
    }

    /**
     * 在指定延迟后执行任务
     *
     * @param task  任务
     * @param delay 延迟时间
     * @param unit  时间单位
     * @return ScheduledFuture 对象
     */
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        totalTasks.incrementAndGet();
        LOGGER.debug("调度任务将在 " + delay + " " + unit + " 后执行");
        
        // 包装任务以跟踪完成状态
        Runnable wrappedTask = () -> {
            try {
                task.run();
                completedTasks.incrementAndGet();
            } catch (Exception e) {
                failedTasks.incrementAndGet();
                LOGGER.error("调度任务执行失败", e);
            }
        };
        
        return ThreadUtil.schedule(scheduler, wrappedTask, delay, unit);
    }

    /**
     * 以固定频率执行任务
     *
     * @param task         任务
     * @param initialDelay 初始延迟
     * @param period       执行周期
     * @param unit         时间单位
     * @return ScheduledFuture 对象
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        totalTasks.incrementAndGet();
        LOGGER.debug("调度任务将以固定频率执行: 初始延迟 " + initialDelay + " " + unit + ", 周期 " + period + " " + unit);
        
        // 包装任务以跟踪完成状态
        Runnable wrappedTask = () -> {
            try {
                task.run();
                completedTasks.incrementAndGet();
            } catch (Exception e) {
                failedTasks.incrementAndGet();
                LOGGER.error("调度任务执行失败", e);
            }
        };
        
        return ThreadUtil.scheduleAtFixedRate(scheduler, wrappedTask, initialDelay, period, unit);
    }

    /**
     * 以固定延迟执行任务
     *
     * @param task         任务
     * @param initialDelay 初始延迟
     * @param delay        延迟时间
     * @param unit         时间单位
     * @return ScheduledFuture 对象
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        totalTasks.incrementAndGet();
        LOGGER.debug("调度任务将以固定延迟执行: 初始延迟 " + initialDelay + " " + unit + ", 延迟 " + delay + " " + unit);
        
        // 包装任务以跟踪完成状态
        Runnable wrappedTask = () -> {
            try {
                task.run();
                completedTasks.incrementAndGet();
            } catch (Exception e) {
                failedTasks.incrementAndGet();
                LOGGER.error("调度任务执行失败", e);
            }
        };
        
        return ThreadUtil.scheduleWithFixedDelay(scheduler, wrappedTask, initialDelay, delay, unit);
    }

    /**
     * 关闭调度器
     */
    public void shutdown() {
        ThreadUtil.shutdown(scheduler);
    }

    /**
     * 立即关闭调度器
     */
    public void shutdownNow() {
        ThreadUtil.shutdownNow(scheduler);
    }

    /**
     * 等待调度器终止
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 是否在超时前终止
     * @throws InterruptedException 如果等待被中断
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return ThreadUtil.awaitTermination(scheduler, timeout, unit);
    }
}