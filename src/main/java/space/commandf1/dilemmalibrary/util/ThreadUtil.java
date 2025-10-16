package space.commandf1.dilemmalibrary.util;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 多线程处理工具类，提供统一的线程管理功能
 */
public class ThreadUtil {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(ThreadUtil.class)
    );

    /**
     * 创建一个固定大小的线程池
     *
     * @param size 线程池大小
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createFixedThreadPool(int size) {
        // 根据 CPU 核心数和任务类型计算最优线程数
        // 对于计算密集型任务，线程数 = CPU 核心数 + 1
        // 对于 I/O 密集型任务，线程数 = CPU 核心数 * (1 + 平均等待时间/平均计算时间)
        int optimalSize = Math.max(size, Runtime.getRuntime().availableProcessors() + 1);
        return new ThreadPoolExecutor(
                optimalSize, optimalSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new CustomThreadFactory("FixedThreadPool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 创建一个可缓存的线程池
     *
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createCachedThreadPool() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new CustomThreadFactory("CachedThreadPool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 创建一个单线程的线程池
     *
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createSingleThreadExecutor() {
        return new ThreadPoolExecutor(
                1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new CustomThreadFactory("SingleThreadExecutor"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 创建一个定时任务线程池
     *
     * @param corePoolSize 核心线程数
     * @return ScheduledExecutorService 定时任务线程池实例
     */
    public static ScheduledExecutorService createScheduledThreadPool(int corePoolSize) {
        int optimalSize = Math.max(corePoolSize, Runtime.getRuntime().availableProcessors());
        return new ScheduledThreadPoolExecutor(
                optimalSize,
                new CustomThreadFactory("ScheduledThreadPool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 创建一个优化的固定大小线程池，使用自定义队列大小
     *
     * @param size       线程池大小
     * @param queueSize  队列大小
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createOptimizedFixedThreadPool(int size, int queueSize) {
        // 根据 CPU 核心数和任务类型计算最优线程数
        int optimalSize = Math.max(size, Runtime.getRuntime().availableProcessors() * 2);
        return new ThreadPoolExecutor(
                optimalSize, optimalSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize > 0 ? queueSize : 1000),
                new CustomThreadFactory("OptimizedFixedThreadPool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 关闭线程池
     *
     * @param executor 线程池实例
     */
    public static void shutdown(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            try {
                // 先尝试优雅关闭
                executor.shutdown();
                // 等待最多60秒让现有任务执行完毕
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 超时则强制关闭
                    executor.shutdownNow();
                    // 再等待60秒确保关闭
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOGGER.error("线程池未能成功关闭");
                    } else {
                        LOGGER.info("线程池已强制关闭");
                    }
                } else {
                    LOGGER.info("线程池已优雅关闭");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                LOGGER.error("线程池关闭过程中被中断", e);
            }
        }
    }

    /**
     * 立即关闭线程池
     *
     * @param executor 线程池实例
     */
    public static void shutdownNow(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            try {
                // 立即关闭并等待最多60秒
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    LOGGER.error("线程池未能成功立即关闭");
                } else {
                    LOGGER.info("线程池已立即关闭");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("线程池立即关闭过程中被中断", e);
            }
        }
    }

    /**
     * 提交一个任务并返回结果
     *
     * @param executor 线程池实例
     * @param task     任务
     * @param <T>      返回结果类型
     * @return Future 对象
     */
    public static <T> Future<T> submit(ExecutorService executor, Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * 提交一个任务并返回结果
     *
     * @param executor 线程池实例
     * @param task     任务
     * @param <T>      返回结果类型
     * @return Future 对象
     */
    public static <T> Future<T> submit(ExecutorService executor, Supplier<T> task) {
        return executor.submit(task::get);
    }

    /**
     * 执行一个任务
     *
     * @param executor 线程池实例
     * @param task     任务
     */
    public static void execute(ExecutorService executor, Runnable task) {
        executor.execute(task);
    }

    /**
     * 在指定延迟后执行任务
     *
     * @param executor 线程池实例
     * @param task     任务
     * @param delay    延迟时间
     * @param unit     时间单位
     * @return ScheduledFuture 对象
     */
    public static ScheduledFuture<?> schedule(ScheduledExecutorService executor, Runnable task, long delay, TimeUnit unit) {
        return executor.schedule(task, delay, unit);
    }

    /**
     * 以固定频率执行任务
     *
     * @param executor 线程池实例
     * @param task     任务
     * @param initialDelay 初始延迟
     * @param period   执行周期
     * @param unit     时间单位
     * @return ScheduledFuture 对象
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(ScheduledExecutorService executor, Runnable task, long initialDelay, long period, TimeUnit unit) {
        return executor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 以固定延迟执行任务
     *
     * @param executor 线程池实例
     * @param task     任务
     * @param initialDelay 初始延迟
     * @param delay    延迟时间
     * @param unit     时间单位
     * @return ScheduledFuture 对象
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(ScheduledExecutorService executor, Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return executor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    /**
     * 等待所有任务完成
     *
     * @param executor 线程池实例
     * @param timeout  超时时间
     * @param unit     时间单位
     * @return 是否在超时前完成
     * @throws InterruptedException 如果等待被中断
     */
    public static boolean awaitTermination(ExecutorService executor, long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    /**
     * 自定义线程工厂类，用于创建带有特定名称前缀的线程
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-thread-" + threadNumber.getAndIncrement());
            t.setDaemon(false);
            return t;
        }
    }
}