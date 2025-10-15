# Processor 接口规范文档

`Processor` 接口及 `AbstractProcessor` 抽象类为处理各种输入数据并返回结果提供了一个通用的规范。

## Processor 接口

```java
public interface Processer<T, R> {
    @Nullable R process(@NotNull T input) throws Exception;
    @NotNull String getName();
}
```

### 方法说明

- `process(@NotNull T input)`: 处理输入数据并返回结果。如果处理过程中发生错误，会抛出异常。
- `getName()`: 返回处理器的唯一标识名称。

## AbstractProcessor 抽象类

```java
public abstract class AbstractProcesser<T, R> implements Processer<T, R> {
    protected AbstractProcesser(@NotNull String name);
    @Override public @NotNull String getName();
    @Override public abstract @Nullable R process(@NotNull T input) throws Exception;
}
```

### 说明

- 提供了处理器名称的存储和获取功能。
- 具体的处理逻辑由子类实现 `process` 方法来完成。

## 使用示例

```java
// 创建自定义处理器
public class MyProcessor extends AbstractProcesser<String, Integer> {
    public MyProcessor() {
        super("MyProcessor");
    }

    @Override
    public Integer process(String input) throws Exception {
        // 实现具体的处理逻辑
        return input.length();
    }
}

// 使用处理器
Processer<String, Integer> processor = new MyProcessor();
Integer result = processor.process("Hello World");
```