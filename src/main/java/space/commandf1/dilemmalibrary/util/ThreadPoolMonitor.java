package space.commandf1.dilemmalibrary.util;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池监控工具类，提供线程池状态监控功能
 */
public class ThreadPoolMonitor {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(ThreadPoolMonitor.class)
    );

    private final ThreadPoolExecutor executor;
    private final String poolName;
    private volatile boolean isShutdown = false;
    private final AtomicInteger totalTasks = new AtomicInteger(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);

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
            long lastCompletedTasks = 0;
            long lastTotalTasks = 0;
            
            while (!isShutdown) {
                try {
                    logThreadPoolStatus(lastCompletedTasks, lastTotalTasks);
                    lastCompletedTasks = executor.getCompletedTaskCount();
                    lastTotalTasks = executor.getTaskCount();
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
        monitorThread.setPriority(Thread.MIN_PRIORITY); // 设置低优先级，避免影响线程池性能
        monitorThread.start();
    }

    /**
     * 记录线程池状态
     */
    private void logThreadPoolStatus(long lastCompletedTasks, long lastTotalTasks) {
        long activeCount = executor.getActiveCount();
        long corePoolSize = executor.getCorePoolSize();
        long maximumPoolSize = executor.getMaximumPoolSize();
        long queueSize = executor.getQueue().size();
        long completedTaskCount = executor.getCompletedTaskCount();
        long taskCount = executor.getTaskCount();
        
        // 计算任务处理速率
        long completedRate = completedTaskCount - lastCompletedTasks;
        long submittedRate = taskCount - lastTotalTasks;
        
        LOGGER.info(String.format(
                "线程池 [%s] 状态: 活跃线程数=%d/%d/%d, 队列大小=%d, 已完成任务数=%d, 总任务数=%d, 任务完成速率=%d/s, 任务提交速率=%d/s",
                poolName,
                activeCount, corePoolSize, maximumPoolSize,
                queueSize,
                completedTaskCount,
                taskCount,
                completedRate / 5, // 5秒间隔
                submittedRate / 5  // 5秒间隔
        ));
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
    
    /**
     * 增加总任务计数
     */
    public void incrementTotalTasks() {
        totalTasks.incrementAndGet();
    }
    
    /**
     * 增加已完成任务计数
     */
    public void incrementCompletedTasks() {
        completedTasks.incrementAndGet();
    }
    
    /**
     * 增加失败任务计数
     */
    public void incrementFailedTasks() {
        failedTasks.incrementAndGet();
    }
    
    /**
     * 获取总任务数
     */
    public int getTotalTasks() {
        return totalTasks.get();
    }
    
    /**
     * 获取已完成任务数
     */
    public int getCompletedTasks() {
        return completedTasks.get();
    }
    
    /**
     * 获取失败任务数
     */
    public int getFailedTasks() {
        return failedTasks.get();
    }
}