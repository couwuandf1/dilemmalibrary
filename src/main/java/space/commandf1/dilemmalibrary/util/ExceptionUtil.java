package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理工具类，提供便捷的异常信息处理方法
 */
public class ExceptionUtil {

    /**
     * 将异常堆栈信息转换为字符串
     *
     * @param throwable 异常对象
     * @return 异常堆栈信息字符串
     */
    @NotNull
    public static String getStackTrace(@NotNull Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 记录异常信息到标准错误输出
     *
     * @param throwable 异常对象
     */
    public static void printStackTrace(@NotNull Throwable throwable) {
        throwable.printStackTrace();
    }
}