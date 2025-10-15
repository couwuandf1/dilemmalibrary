# ProcesserManager 处理器管理器文档

`ProcesserManager` 用于注册和管理各种处理器实例，并提供统一的执行接口。

## ProcesserManager 类

```java
public class ProcesserManager {
    public void registerProcesser(@NotNull Processer<?, ?> processer);
    public @Nullable Processer<?, ?> getProcesser(@NotNull String name);
    public @Nullable <T, R> R executeProcesser(@NotNull String name, @NotNull T input, @NotNull LoggerProvider<?> loggerProvider);
}
```

### 方法说明

- `registerProcesser(@NotNull Processer<?, ?> processer)`: 注册一个处理器实例。
- `getProcesser(@NotNull String name)`: 根据名称获取已注册的处理器。
- `executeProcesser(...)`: 执行指定名称的处理器，传入输入数据和日志提供者。

### 使用示例

```java
// 创建处理器管理器
ProcesserManager manager = new ProcesserManager();

// 注册处理器
Processer<InputStream, MyConfig> yamlProcesser = new YamlConfigProcesser<>(MyConfig.class, loggerProvider);
manager.registerProcesser(yamlProcesser);

// 执行处理器
MyConfig config = manager.executeProcesser("YAML Config Processor", inputStream, loggerProvider);

// 检查结果
if (config != null) {
    // 处理配置
} else {
    // 处理错误
}
```

### 错误处理

在执行处理器时，如果发生异常，`ProcesserManager` 会自动捕获并记录日志：

```java
// 如果处理器未找到
// 日志输出: "Processer not found: " + name

// 如果处理器执行出错
// 日志输出: "Error executing processer " + name + ": " + e.getMessage()
```