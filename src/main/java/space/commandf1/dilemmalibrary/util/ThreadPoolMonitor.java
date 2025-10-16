package space.commandf1.dilemmalibrary.util;

import lombok.Getter;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池监控工具类，提供线程池状态监控功能
 */
public class ThreadPoolMonitor {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(ThreadPoolMonitor.class)
    );

    @Getter
    private final ThreadPoolExecutor executor;
    private final String poolName;
    private volatile boolean isShutdown = false;

    /**
     * 构造函数
     *
     * @param executor 线程池实例
     * @param poolName 线程池名称
     */
    public ThreadPoolMonitor(ThreadPoolExecutor executor, String poolName) {
        this.executor = executor;
        this.poolName = poolName;
        startMonitoring();
    }

    /**
     * 开始监控线程池状态
     */
    private void startMonitoring() {
        Thread monitorThread = new Thread(() -> {
            while (!isShutdown) {
                try {
                    logThreadPoolStatus();
                    TimeUnit.SECONDS.sleep(5); // 每5秒输出一次状态
                } catch (InterruptedException e) {
                    LOGGER.error("监控线程被中断", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName(poolName + "-monitor");
        monitorThread.start();
    }

    /**
     * 记录线程池状态
     */
    private void logThreadPoolStatus() {
        LOGGER.info(String.format("线程池 [%s] 状态: 活跃线程数=%d, 核心线程数=%d, 最大线程数=%d, 队列大小=%d, 已完成任务数=%d, 总任务数=%d",
                poolName,
                executor.getActiveCount(),
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getQueue().size(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount()));
    }

    /**
     * 关闭监控
     */
    public void shutdown() {
        isShutdown = true;
        LOGGER.info("线程池监控 [" + poolName + "] 已关闭");
    }

    /**
     * 获取线程池实例
     *
     * @return ThreadPoolExecutor 线程池实例
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}