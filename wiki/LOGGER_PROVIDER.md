 LoggerProvider 日志记录接口文档

`LoggerProvider` 为应用程序提供了一个统一且可扩展的日志记录接口，支持多种日志框架。

## LoggerProvider 抽象类

```java
public abstract class LoggerProvider<T> implements IProvider<T> {
    public LoggerProvider(@NotNull T logger);
    
    // 基础日志方法
    public abstract void log(String message);
    public abstract void log(String message, Throwable throwable);
    
    // 不同级别的日志方法
    public void debug(String message);
    public void info(String message);
    public void warn(String message);
    public void error(String message);
    
    // 带异常的不同级别日志方法
    public void debug(String message, Throwable throwable);
    public void info(String message, Throwable throwable);
    public void warn(String message, Throwable throwable);
    public void error(String message, Throwable throwable);
    
    // 特殊功能方法
    public void logWithStackTrace(String message, Throwable throwable);
    
    @Override public @NotNull T get();
}
```

### 方法说明

- `log(String message)`: 记录普通日志信息。
- `log(String message, Throwable throwable)`: 记录带异常信息的日志。
- `debug/info/warn/error(String message)`: 记录不同级别的日志信息。
- `debug/info/warn/error(String message, Throwable throwable)`: 记录带异常信息的不同级别日志。
- `logWithStackTrace(String message, Throwable throwable)`: 记录带完整堆栈跟踪信息的日志。
- `get()`: 获取底层的日志记录器实例。

## 内置实现

### LombokLoggerProvider (SLF4J)

```java
public static class LombokLoggerProvider extends LoggerProvider<org.slf4j.Logger>
```

使用 SLF4J 作为日志记录框架。

### DefaultLoggerProvider (Java Util Logging)

```java
public static class DefaultLoggerProvider extends LoggerProvider<java.util.logging.Logger>
```

使用 Java 内置的日志记录框架。

## 使用示例

```java
// 使用 SLF4J
LoggerProvider<org.slf4j.Logger> slf4jProvider = 
    new LoggerProvider.LombokLoggerProvider(LoggerFactory.getLogger("MyApp"));

// 使用 Java Util Logging
LoggerProvider<java.util.logging.Logger> defaultProvider = 
    new LoggerProvider.DefaultLoggerProvider(java.util.logging.Logger.getLogger("MyApp"));

// 记录日志
slf4jProvider.log("Application started");
defaultProvider.log("Application started");

// 记录异常日志
try {
    // some code that may throw exception
} catch (Exception e) {
    slf4jProvider.log("Error occurred", e);
    defaultProvider.log("Error occurred", e);
}
```