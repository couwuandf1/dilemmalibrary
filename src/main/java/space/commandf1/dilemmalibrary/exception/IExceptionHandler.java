package space.commandf1.dilemmalibrary.exception;

/**
 * 通用异常处理接口，定义了异常处理的基本方法
 */
public interface IExceptionHandler {

    /**
     * 处理异常
     *
     * @param throwable 异常信息
     */
    void handleException(Throwable throwable);

    /**
     * 处理带消息的异常
     *
     * @param message   异常消息
     * @param throwable 异常信息
     */
    void handleException(String message, Throwable throwable);
}