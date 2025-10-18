# ConfigManager - 高性能配置管理器

## 简介

ConfigManager是一个高性能的配置管理器，提供统一的配置管理功能。它支持多种配置格式、热重载和变更监听机制，针对嵌入式系统进行了优化，确保在资源受限环境下的低内存占用和高并发性能。

## 主要特性

- 统一的配置管理接口
- 支持多种配置格式（目前支持YAML）
- 配置验证机制
- 配置变更监听
- 热重载支持
- 性能监控
- 线程安全设计

## 使用方法

### 1. 定义配置类

首先定义你的配置类，使用Lombok的@Data注解简化代码：

```java
@Data
public class AppConfig {
    private String appName;
    private int port;
    private boolean debug;
    private DatabaseConfig database;
}

@Data
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
}
```

### 2. 创建ConfigManager实例

```java
ConfigManager<AppConfig> configManager = new ConfigManager<>(AppConfig.class, "app.yaml");
```

### 3. 添加配置验证器

```java
// 添加端口验证器
configManager.addConfigValidator("portValidator", config -> config.getPort() > 0 && config.getPort() < 65536);

// 添加应用名称验证器
configManager.addConfigValidator("appNameValidator", config -> config.getAppName() != null && !config.getAppName().isEmpty());
```

### 4. 添加配置变更监听器

```java
configManager.addConfigChangeListener("configLogger", config -> 
    System.out.println("配置已变更: " + config.getAppName() + " on port " + config.getPort())
);
```

### 5. 加载配置

```java
// 从文件加载配置
FileInputStream fis = new FileInputStream("app.yaml");
AppConfig config = configManager.process(fis);

// 或者从字符串加载配置
String yamlConfig = "appName: MyApp\nport: 8080\ndebug: true\n...";
AppConfig config = configManager.process(
    new ByteArrayInputStream(yamlConfig.getBytes(StandardCharsets.UTF_8))
);
```

### 6. 启用热重载

```java
// 创建调度器
ScheduledExecutorService scheduler = ThreadUtil.createScheduledThreadPool(2);

// 启用热重载，每5秒检查一次
configManager.enableHotReload(scheduler, 5000);
```

### 7. 获取当前配置

```java
AppConfig currentConfig = configManager.getCurrentConfig();
```

### 8. 性能监控

```java
// 获取性能监控器
ConfigManagerMonitor monitor = configManager.getMonitor();

// 打印性能报告
monitor.printPerformanceReport();

// 获取统计信息
long loadCount = configManager.getConfigLoadCount();
long reloadCount = configManager.getConfigReloadCount();
```

## API参考

### ConfigManager类

#### 构造函数
```java
public ConfigManager(@NotNull Class<T> configClass, @NotNull String configPath)
```

#### 主要方法
- `process(@NotNull InputStream input)`: 加载并处理配置
- `getCurrentConfig()`: 获取当前配置
- `addConfigChangeListener(@NotNull String listenerName, @NotNull Consumer<T> listener)`: 添加配置变更监听器
- `removeConfigChangeListener(@NotNull String listenerName)`: 移除配置变更监听器
- `addConfigValidator(@NotNull String validatorName, @NotNull Predicate<T> validator)`: 添加配置验证器
- `removeConfigValidator(@NotNull String validatorName)`: 移除配置验证器
- `enableHotReload(@NotNull ScheduledExecutorService scheduler, long interval)`: 启用配置热重载
- `disableHotReload()`: 禁用配置热重载
- `getMonitor()`: 获取性能监控器

#### 统计方法
- `getConfigLoadCount()`: 获取配置加载次数
- `getConfigReloadCount()`: 获取配置重载次数
- `getListenerNotificationCount()`: 获取监听器通知次数
- `getListenerCount()`: 获取监听器数量
- `getValidatorCount()`: 获取验证器数量

### ConfigManagerMonitor类

#### 主要方法
- `printPerformanceReport()`: 打印性能报告
- `getAverageLoadTime()`: 获取平均加载时间
- `getMaxLoadTime()`: 获取最大加载时间
- `getMinLoadTime()`: 获取最小加载时间
- `getLoadCount()`: 获取加载次数
- `getErrorCount()`: 获取错误总数
- `getErrorRate()`: 获取错误率

## 最佳实践

1. **配置类设计**: 使用Lombok的@Data注解简化配置类代码
2. **验证器使用**: 为关键配置项添加验证器，确保配置的有效性
3. **监听器管理**: 合理使用配置变更监听器，在配置变更时执行必要的操作
4. **热重载配置**: 根据实际需求设置合适的热重载间隔
5. **性能监控**: 定期检查性能报告，优化配置加载性能
6. **资源管理**: 在应用关闭时调用`disableHotReload()`方法释放资源