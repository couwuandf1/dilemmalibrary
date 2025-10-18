package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 配置管理器性能监控工具类
 * 提供对ConfigManager的性能监控功能
 */
public class ConfigManagerMonitor {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(ConfigManagerMonitor.class)
    );

    private final String configPath;
    private final AtomicLong totalLoadTime;
    private final AtomicLong maxLoadTime;
    private final AtomicLong minLoadTime;
    private final AtomicLong loadCount;
    
    // 错误统计
    private final AtomicLong errorCount;
    private final AtomicLong validationErrorCount;
    private final AtomicLong loadErrorCount;

    /**
     * 构造函数
     *
     * @param configPath 配置路径
     */
    public ConfigManagerMonitor(@NotNull String configPath) {
        this.configPath = configPath;
        this.totalLoadTime = new AtomicLong(0);
        this.maxLoadTime = new AtomicLong(0);
        this.minLoadTime = new AtomicLong(Long.MAX_VALUE);
        this.loadCount = new AtomicLong(0);
        this.errorCount = new AtomicLong(0);
        this.validationErrorCount = new AtomicLong(0);
        this.loadErrorCount = new AtomicLong(0);
    }

    /**
     * 记录配置加载时间
     *
     * @param loadTime 加载时间（毫秒）
     */
    public void recordLoadTime(long loadTime) {
        // 更新加载时间统计
        totalLoadTime.addAndGet(loadTime);
        loadCount.incrementAndGet();
        
        // 更新最大加载时间
        maxLoadTime.accumulateAndGet(loadTime, Math::max);
        
        // 更新最小加载时间
        minLoadTime.accumulateAndGet(loadTime, Math::min);
        
        LOGGER.debug("配置加载时间统计 [" + configPath + "]: " + loadTime + "ms");
    }

    /**
     * 记录配置加载错误
     */
    public void recordLoadError() {
        errorCount.incrementAndGet();
        loadErrorCount.incrementAndGet();
        LOGGER.error("配置加载错误 [" + configPath + "]");
    }

    /**
     * 记录配置验证错误
     */
    public void recordValidationError() {
        errorCount.incrementAndGet();
        validationErrorCount.incrementAndGet();
        LOGGER.error("配置验证错误 [" + configPath + "]");
    }

    /**
     * 获取平均加载时间
     *
     * @return 平均加载时间（毫秒）
     */
    public double getAverageLoadTime() {
        long count = loadCount.get();
        return count == 0 ? 0.0 : (double) totalLoadTime.get() / count;
    }

    /**
     * 获取最大加载时间
     *
     * @return 最大加载时间（毫秒）
     */
    public long getMaxLoadTime() {
        return maxLoadTime.get();
    }

    /**
     * 获取最小加载时间
     *
     * @return 最小加载时间（毫秒）
     */
    public long getMinLoadTime() {
        long min = minLoadTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * 获取加载次数
     *
     * @return 加载次数
     */
    public long getLoadCount() {
        return loadCount.get();
    }

    /**
     * 获取错误总数
     *
     * @return 错误总数
     */
    public long getErrorCount() {
        return errorCount.get();
    }

    /**
     * 获取加载错误数
     *
     * @return 加载错误数
     */
    public long getLoadErrorCount() {
        return loadErrorCount.get();
    }

    /**
     * 获取验证错误数
     *
     * @return 验证错误数
     */
    public long getValidationErrorCount() {
        return validationErrorCount.get();
    }

    /**
     * 获取错误率
     *
     * @return 错误率（0-1之间）
     */
    public double getErrorRate() {
        long total = loadCount.get();
        return total == 0 ? 0.0 : (double) errorCount.get() / total;
    }

    /**
     * 打印性能报告
     */
    public void printPerformanceReport() {
        LOGGER.info("=== 配置管理器性能报告 [" + configPath + "] ===");
        LOGGER.info("加载次数: " + getLoadCount());
        LOGGER.info("平均加载时间: " + String.format("%.2f", getAverageLoadTime()) + "ms");
        LOGGER.info("最大加载时间: " + getMaxLoadTime() + "ms");
        LOGGER.info("最小加载时间: " + getMinLoadTime() + "ms");
        LOGGER.info("错误总数: " + getErrorCount());
        LOGGER.info("加载错误数: " + getLoadErrorCount());
        LOGGER.info("验证错误数: " + getValidationErrorCount());
        LOGGER.info("错误率: " + String.format("%.2f%%", getErrorRate() * 100));
        LOGGER.info("=====================================");
    }
    
    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        totalLoadTime.set(0);
        maxLoadTime.set(0);
        minLoadTime.set(Long.MAX_VALUE);
        loadCount.set(0);
        errorCount.set(0);
        validationErrorCount.set(0);
        loadErrorCount.set(0);
        LOGGER.info("性能统计信息已重置 [" + configPath + "]");
    }
}