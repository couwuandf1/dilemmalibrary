package space.commandf1.dilemmalibrary.exception;

/**
 * 业务异常类，继承自 BaseException，用于处理业务逻辑中的异常情况
 */
public class BusinessException extends BaseException {

    /**
     * 构造函数，接收异常信息
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 构造函数，接收异常信息和异常原因
     *
     * @param message 异常信息
     * @param cause   异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}