package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractProcesser<T, R> implements Processer<T, R> {
    private final String name;

    protected AbstractProcesser(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * 具体的处理逻辑由子类实现
     *
     * @param input 输入数据
     * @return 处理结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public abstract @Nullable R process(@NotNull T input) throws Exception;
}