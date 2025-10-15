package space.commandf1.dilemmalibrary.provider.logger;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.IProvider;

/**
 * 日志提供者抽象类，实现了 IProvider 接口，用于提供统一的日志记录功能
 *
 * @param <T> 日志记录器类型
 */
@EqualsAndHashCode
@ToString
public abstract class LoggerProvider<T> implements IProvider<T> {

    private final T logger;

    /**
     * 构造函数，初始化日志记录器
     *
     * @param logger 日志记录器实例
     */
    public LoggerProvider(@NotNull T logger) {
        this.logger = logger;
    }

    /**
     * 记录日志信息
     *
     * @param message 日志信息
     */
    public abstract void log(String message);

    /**
     * 记录带异常信息的日志
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public abstract void log(String message, Throwable throwable);

    @Override
    public @NotNull T get() {
        return this.logger;
    }

    /**
     * SLF4J 日志提供者实现
     */
    public static class LombokLoggerProvider extends LoggerProvider<org.slf4j.Logger> {
        public LombokLoggerProvider(org.slf4j.@NotNull Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            this.get().info(message);
        }

        @Override
        public void log(String message, Throwable throwable) {
            this.get().info(message, throwable);
        }
    }

    /**
     * Java Util Logging 日志提供者实现
     */
    public static class DefaultLoggerProvider extends LoggerProvider<java.util.logging.Logger> {

        public DefaultLoggerProvider(java.util.logging.Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            this.get().info(message);
        }

        @Override
        public void log(String message, Throwable throwable) {
            this.get().log(java.util.logging.Level.INFO, message, throwable);
        }
    }
}