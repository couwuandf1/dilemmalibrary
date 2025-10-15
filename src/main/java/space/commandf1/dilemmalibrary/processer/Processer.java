package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 通用处理器接口，定义了处理输入数据并返回结果的标准方法
 *
 * @param <T> 输入数据类型
 * @param <R> 处理结果类型
 */
public interface Processer<T, R> {
    /**
     * 处理输入数据并返回结果
     *
     * @param input 输入数据
     * @return 处理结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Nullable R process(@NotNull T input) throws Exception;

    /**
     * 获取处理器的唯一标识名称
     *
     * @return 处理器名称
     */
    @NotNull String getName();
}