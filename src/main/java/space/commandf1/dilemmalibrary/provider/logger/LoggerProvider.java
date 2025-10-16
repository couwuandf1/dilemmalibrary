package space.commandf1.dilemmalibrary.provider.logger;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.IProvider;
import space.commandf1.dilemmalibrary.util.ExceptionUtil;

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
     * 记录调试级别日志信息
     *
     * @param message 日志信息
     */
    public void debug(String message) {
        this.log("[DEBUG] " + message);
    }

    /**
     * 记录信息级别日志信息
     *
     * @param message 日志信息
     */
    public void info(String message) {
        this.log("[INFO] " + message);
    }

    /**
     * 记录警告级别日志信息
     *
     * @param message 日志信息
     */
    public void warn(String message) {
        this.log("[WARN] " + message);
    }

    /**
     * 记录错误级别日志信息
     *
     * @param message 日志信息
     */
    public void error(String message) {
        this.log("[ERROR] " + message);
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

    /**
     * 记录带异常信息的调试日志
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public void debug(String message, Throwable throwable) {
        this.log("[DEBUG] " + message, throwable);
    }

    /**
     * 记录带异常信息的信息日志
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public void info(String message, Throwable throwable) {
        this.log("[INFO] " + message, throwable);
    }

    /**
     * 记录带异常信息的警告日志
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public void warn(String message, Throwable throwable) {
        this.log("[WARN] " + message, throwable);
    }

    /**
     * 记录带异常信息的错误日志
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public void error(String message, Throwable throwable) {
        this.log("[ERROR] " + message, throwable);
    }

    /**
     * 记录带异常信息的日志，并包含完整的堆栈跟踪信息
     *
     * @param message   日志信息
     * @param throwable 异常信息
     */
    public void logWithStackTrace(String message, Throwable throwable) {
        String stackTrace = ExceptionUtil.getStackTrace(throwable);
        this.log(message + "\n" + stackTrace);
    }

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

        @Override
        public void debug(String message) {
            this.get().debug(message);
        }

        @Override
        public void info(String message) {
            this.get().info(message);
        }

        @Override
        public void warn(String message) {
            this.get().warn(message);
        }

        @Override
        public void error(String message) {
            this.get().error(message);
        }

        @Override
        public void debug(String message, Throwable throwable) {
            this.get().debug(message, throwable);
        }

        @Override
        public void info(String message, Throwable throwable) {
            this.get().info(message, throwable);
        }

        @Override
        public void warn(String message, Throwable throwable) {
            this.get().warn(message, throwable);
        }

        @Override
        public void error(String message, Throwable throwable) {
            this.get().error(message, throwable);
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

        @Override
        public void debug(String message) {
            this.get().fine(message);
        }

        @Override
        public void info(String message) {
            this.get().info(message);
        }

        @Override
        public void warn(String message) {
            this.get().warning(message);
        }

        @Override
        public void error(String message) {
            this.get().severe(message);
        }

        @Override
        public void debug(String message, Throwable throwable) {
            this.get().log(java.util.logging.Level.FINE, message, throwable);
        }

        @Override
        public void info(String message, Throwable throwable) {
            this.get().log(java.util.logging.Level.INFO, message, throwable);
        }

        @Override
        public void warn(String message, Throwable throwable) {
            this.get().log(java.util.logging.Level.WARNING, message, throwable);
        }

        @Override
        public void error(String message, Throwable throwable) {
            this.get().log(java.util.logging.Level.SEVERE, message, throwable);
        }
    }
}