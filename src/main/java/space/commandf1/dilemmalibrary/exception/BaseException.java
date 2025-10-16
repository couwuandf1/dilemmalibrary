package space.commandf1.dilemmalibrary.exception;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

/**
 * 基础异常类，继承自 RuntimeException，提供统一的异常处理功能
 */
public class BaseException extends RuntimeException {
    private static final LoggerProvider<?> LOGGER = new LoggerProvider.LombokLoggerProvider(
            org.slf4j.LoggerFactory.getLogger(BaseException.class)
    );

    /**
     * 构造函数，接收异常信息
     *
     * @param message 异常信息
     */
    public BaseException(String message) {
        super(message);
        LOGGER.error(message);
    }

    /**
     * 构造函数，接收异常信息和异常原因
     *
     * @param message 异常信息
     * @param cause   异常原因
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        LOGGER.error(message, cause);
    }
}