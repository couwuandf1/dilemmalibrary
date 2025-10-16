package space.commandf1.dilemmalibrary.util;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.*;

/**
 * 高效的任务调度器，提供灵活的任务调度功能
 */
public class TaskScheduler {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(TaskScheduler.class)
    );

    private final ScheduledExecutorService scheduler;

    /**
     * 构造函数，创建一个默认的调度器
     */
    public TaskScheduler() {
        this.scheduler = ThreadUtil.createScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 构造函数，创建一个指定核心线程数的调度器
     *
     * @param corePoolSize 核心线程数
     */
    public TaskScheduler(int corePoolSize) {
        this.scheduler = ThreadUtil.createScheduledThreadPool(corePoolSize);
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
        LOGGER.info("调度任务将在 " + delay + " " + unit + " 后执行");
        return ThreadUtil.schedule(scheduler, task, delay, unit);
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
        LOGGER.info("调度任务将以固定频率执行: 初始延迟 " + initialDelay + " " + unit + ", 周期 " + period + " " + unit);
        return ThreadUtil.scheduleAtFixedRate(scheduler, task, initialDelay, period, unit);
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
        LOGGER.info("调度任务将以固定延迟执行: 初始延迟 " + initialDelay + " " + unit + ", 延迟 " + delay + " " + unit);
        return ThreadUtil.scheduleWithFixedDelay(scheduler, task, initialDelay, delay, unit);
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