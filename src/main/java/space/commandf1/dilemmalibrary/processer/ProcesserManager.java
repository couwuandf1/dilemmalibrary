package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcesserManager {
    private final Map<String, Processer<?, ?>> processers = new ConcurrentHashMap<>();

    /**
     * 注册一个处理器
     *
     * @param processer 要注册的处理器
     */
    public void registerProcesser(@NotNull Processer<?, ?> processer) {
        this.processers.put(processer.getName(), processer);
    }

    /**
     * 根据名称获取处理器
     *
     * @param name 处理器名称
     * @return 处理器实例或null（如果未找到）
     */
    @Nullable
    public Processer<?, ?> getProcesser(@NotNull String name) {
        return this.processers.get(name);
    }

    /**
     * 执行指定名称的处理器
     *
     * @param name  处理器名称
     * @param input 输入数据
     * @return 处理结果或null（如果处理器未找到或处理失败）
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T, R> R executeProcesser(@NotNull String name, @NotNull T input, @NotNull LoggerProvider<?> loggerProvider) {
        Processer<T, R> processer = (Processer<T, R>) this.getProcesser(name);
        if (processer == null) {
            loggerProvider.log("Processer not found: " + name);
            return null;
        }

        try {
            return processer.process(input);
        } catch (Exception e) {
            loggerProvider.log("Error executing processer " + name + ": " + e.getMessage());
            return null;
        }
    }
}
