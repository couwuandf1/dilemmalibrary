package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高性能分布式锁工具类，提供可重入的分布式锁功能
 * 针对嵌入式系统优化，确保在资源受限环境下的低内存占用和高并发性能
 */
public class DistributedLockUtil {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(DistributedLockUtil.class)
    );

    // 使用ConcurrentHashMap存储锁信息
    private final ConcurrentHashMap<String, DistributedLock> locks = new ConcurrentHashMap<>();
    
    // 使用线程池处理锁超时检查
    private final ScheduledExecutorService timeoutExecutor;
    
    // 性能监控计数器
    private final AtomicLong totalLocks = new AtomicLong(0);
    private final AtomicLong successfulLocks = new AtomicLong(0);
    private final AtomicLong failedLocks = new AtomicLong(0);
    private final AtomicLong lockTimeouts = new AtomicLong(0);
    
    /**
     * 构造函数，创建一个默认的分布式锁工具
     */
    public DistributedLockUtil() {
        // 针对嵌入式系统优化，使用单线程调度器处理超时检查
        this.timeoutExecutor = ThreadUtil.createScheduledThreadPool(1);
    }
    
    /**
     * 获取分布式锁
     *
     * @param lockKey 锁键
     * @return 分布式锁实例
     */
    public DistributedLock getLock(@NotNull String lockKey) {
        return locks.computeIfAbsent(lockKey, k -> new DistributedLock(k, this));
    }
    
    /**
     * 尝试获取锁
     *
     * @param lockKey 锁键
     * @param timeout 超时时间（毫秒）
     * @return 是否获取成功
     * @throws InterruptedException 如果线程在等待锁时被中断
     */
    public boolean tryLock(@NotNull String lockKey, long timeout) throws InterruptedException {
        return getLock(lockKey).tryLock(timeout, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 释放锁
     *
     * @param lockKey 锁键
     */
    public void unlock(@NotNull String lockKey) {
        DistributedLock lock = locks.get(lockKey);
        if (lock != null) {
            lock.unlock();
        }
    }
    
    /**
     * 关闭分布式锁工具
     */
    public void shutdown() {
        // 关闭超时检查调度器
        ThreadUtil.shutdown(timeoutExecutor);
        
        // 清理所有锁
        locks.clear();
        
        LOGGER.info("分布式锁工具已关闭");
    }
    
    /**
     * 获取总锁请求次数
     */
    public long getTotalLocks() {
        return totalLocks.get();
    }
    
    /**
     * 获取成功获取锁的次数
     */
    public long getSuccessfulLocks() {
        return successfulLocks.get();
    }
    
    /**
     * 获取失败锁的次数
     */
    public long getFailedLocks() {
        return failedLocks.get();
    }
    
    /**
     * 获取锁超时次数
     */
    public long getLockTimeouts() {
        return lockTimeouts.get();
    }
    
    /**
     * 计算锁获取成功率
     *
     * @return 锁获取成功率（0-1之间）
     */
    public double getSuccessRate() {
        long total = totalLocks.get();
        long successful = successfulLocks.get();
        return total == 0 ? 0.0 : (double) successful / total;
    }
    
    /**
     * 内部使用的分布式锁实现
     */
    public static class DistributedLock implements Lock {
        private final String lockKey;
        private final DistributedLockUtil parent;
        private final ReentrantLock internalLock;
        private final AtomicBoolean isLocked;
        private final ThreadLocal<Integer> holdCount;
        private volatile ScheduledFuture<?> timeoutTask;
        
        DistributedLock(String lockKey, DistributedLockUtil parent) {
            this.lockKey = lockKey;
            this.parent = parent;
            this.internalLock = new ReentrantLock();
            this.isLocked = new AtomicBoolean(false);
            this.holdCount = ThreadLocal.withInitial(() -> 0);
        }
        
        @Override
        public void lock() {
            try {
                lockInterruptibly();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("线程在获取锁时被中断", e);
            }
        }
        
        @Override
        public void lockInterruptibly() throws InterruptedException {
            parent.totalLocks.incrementAndGet();
            
            // 如果当前线程已经持有锁，增加持有计数
            if (isHeldByCurrentThread()) {
                holdCount.set(holdCount.get() + 1);
                parent.successfulLocks.incrementAndGet();
                return;
            }
            
            // 尝试获取内部锁
            internalLock.lockInterruptibly();
            
            try {
                // 设置锁状态
                isLocked.set(true);
                holdCount.set(1);
                parent.successfulLocks.incrementAndGet();
                LOGGER.debug("成功获取分布式锁: " + lockKey);
            } catch (Exception e) {
                // 如果获取失败，释放内部锁
                internalLock.unlock();
                parent.failedLocks.incrementAndGet();
                throw e;
            }
        }
        
        @Override
        public boolean tryLock() {
            parent.totalLocks.incrementAndGet();
            
            // 如果当前线程已经持有锁，增加持有计数
            if (isHeldByCurrentThread()) {
                holdCount.set(holdCount.get() + 1);
                parent.successfulLocks.incrementAndGet();
                return true;
            }
            
            // 尝试获取内部锁
            if (internalLock.tryLock()) {
                try {
                    // 设置锁状态
                    isLocked.set(true);
                    holdCount.set(1);
                    parent.successfulLocks.incrementAndGet();
                    LOGGER.debug("成功尝试获取分布式锁: " + lockKey);
                    return true;
                } catch (Exception e) {
                    // 如果获取失败，释放内部锁
                    internalLock.unlock();
                    parent.failedLocks.incrementAndGet();
                }
            } else {
                parent.failedLocks.incrementAndGet();
            }
            
            return false;
        }
        
        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            parent.totalLocks.incrementAndGet();
            
            // 如果当前线程已经持有锁，增加持有计数
            if (isHeldByCurrentThread()) {
                holdCount.set(holdCount.get() + 1);
                parent.successfulLocks.incrementAndGet();
                return true;
            }
            
            // 尝试获取内部锁
            if (internalLock.tryLock(time, unit)) {
                try {
                    // 设置锁状态
                    isLocked.set(true);
                    holdCount.set(1);
                    parent.successfulLocks.incrementAndGet();
                    LOGGER.debug("成功尝试获取分布式锁: " + lockKey + " (超时: " + time + " " + unit + ")");
                    return true;
                } catch (Exception e) {
                    // 如果获取失败，释放内部锁
                    internalLock.unlock();
                    parent.failedLocks.incrementAndGet();
                }
            } else {
                parent.failedLocks.incrementAndGet();
                parent.lockTimeouts.incrementAndGet();
            }
            
            return false;
        }
        
        @Override
        public void unlock() {
            // 检查当前线程是否持有锁
            if (!isHeldByCurrentThread()) {
                throw new IllegalMonitorStateException("当前线程不持有此锁: " + lockKey);
            }
            
            // 减少持有计数
            int count = holdCount.get();
            if (count > 1) {
                holdCount.set(count - 1);
                return;
            }
            
            try {
                // 取消超时任务
                if (timeoutTask != null && !timeoutTask.isDone()) {
                    timeoutTask.cancel(false);
                }
                
                // 重置锁状态
                isLocked.set(false);
                holdCount.remove();
                
                LOGGER.debug("成功释放分布式锁: " + lockKey);
            } finally {
                // 释放内部锁
                internalLock.unlock();
            }
        }
        
        @Override
        public Condition newCondition() {
            return internalLock.newCondition();
        }
        
        /**
         * 检查当前线程是否持有锁
         *
         * @return 是否持有锁
         */
        public boolean isHeldByCurrentThread() {
            return internalLock.isHeldByCurrentThread() && isLocked.get();
        }
        
        /**
         * 检查锁是否被任何线程持有
         *
         * @return 是否被持有
         */
        public boolean isLocked() {
            return isLocked.get();
        }
        
        /**
         * 获取锁键
         *
         * @return 锁键
         */
        public String getLockKey() {
            return lockKey;
        }
        
        /**
         * 设置超时时间
         *
         * @param timeout 超时时间
         * @param unit 时间单位
         */
        public void setTimeout(long timeout, @NotNull TimeUnit unit) {
            if (isHeldByCurrentThread()) {
                // 取消之前的超时任务
                if (timeoutTask != null && !timeoutTask.isDone()) {
                    timeoutTask.cancel(false);
                }
                
                // 安排新的超时任务
                timeoutTask = parent.timeoutExecutor.schedule(() -> {
                    if (isLocked.get()) {
                        LOGGER.warn("分布式锁超时，自动释放: " + lockKey);
                        try {
                            unlock();
                        } catch (Exception e) {
                            LOGGER.error("自动释放锁失败: " + lockKey, e);
                        }
                    }
                }, timeout, unit);
            }
        }
    }
}