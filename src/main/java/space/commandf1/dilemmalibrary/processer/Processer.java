package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Processer<T, R> {
    /**
     * 处理输入数据并返回结果
     * @param input 输入数据
     * @return 处理结果
     * @throws Exception 处理过抛出的异常
     */
    @Nullable R process(@NotNull T input) throws Exception;

    /**
     * @return 处理器名称
     */
    @NotNull String getName();
}
