# DilemmaLibrary

DilemmaLibrary 是一个Java库，提供了通用的处理器接口、日志记录功能和实用工具类，帮助开发者构建可扩展的应用程序。该库针对嵌入式系统和高并发场景进行了深度优化，确保在资源受限环境下的低内存占用和高性能表现。

## 功能特性

### 1. Processor处理器系统（高性能数据处理）
- **统一处理接口**: 提供标准化的`Processer<T, R>`接口，支持各种数据处理场景
- **抽象实现**: `AbstractProcesser`提供基础实现，简化自定义处理器开发
- **YAML处理器**: 内置`YamlConfigProcesser`支持YAML配置文件解析
- **性能优化**: 采用无锁化设计，针对高并发场景优化，减少内存分配和GC压力

### 2. LoggerProvider日志系统（统一日志接口）
- **多框架支持**: 统一的日志记录接口，支持SLF4J和Java Util Logging
- **性能优化**: 减少日志记录时的对象创建，提供高效的日志输出机制
- **扩展性**: 易于扩展支持其他日志框架

### 3. 实用工具类（高性能工具集合）
- **异常处理**: `ExceptionUtil`提供高效的异常堆栈信息处理
- **通用工具**: `CommonUtil`包含空值检查、集合判断等常用方法
- **数组工具**: `ArrayUtil`提供数组操作优化方法
- **字符串工具**: `StringUtil`提供字符串处理和格式化功能
- **文件工具**: `FileUtil`简化文件操作，提供高效读写方法

### 4. ProcessorManager处理器管理（高性能管理器）
- **统一管理**: 提供处理器注册、查找和执行的统一入口
- **性能优化**: 使用`ConcurrentHashMap`实现无锁化访问，针对高并发优化
- **资源优化**: 针对嵌入式系统优化，减少内存占用和资源消耗

### 5. 高性能缓存系统（多策略缓存实现）
- **LRU缓存**: `LRUCache`实现最近最少使用算法，支持性能监控
- **过期缓存**: `ExpiringCache`支持自动过期机制，防止内存泄漏
- **计算缓存**: `ComputingCache`支持自动加载和缓存穿透防护
- **性能优化**: 针对嵌入式系统优化，使用原子操作减少锁竞争，提供低延迟访问

### 6. 线程工具（高效并发处理）
- **线程池管理**: `ThreadUtil`提供多种线程池创建和管理方法
- **任务调度**: `TaskScheduler`支持灵活的任务调度功能
- **线程池监控**: `ThreadPoolMonitor`提供实时性能监控
- **性能优化**: 针对高并发场景优化，使用优化的线程池参数和队列策略

### 7. 事件总线（高并发事件处理）
- **异步处理**: `EventBusUtil`提供高性能异步事件发布订阅机制
- **多事件支持**: 支持多种事件类型和复杂事件处理逻辑
- **性能监控**: 内置性能监控功能，支持事件处理成功率统计
- **性能优化**: 针对嵌入式系统优化，使用无锁数据结构和高效的线程池

### 8. 分布式锁（可重入分布式锁）
- **可重入支持**: `DistributedLockUtil`提供可重入的分布式锁实现
- **超时机制**: 支持自动超时释放，防止死锁
- **性能监控**: 内置性能监控功能，支持锁获取成功率统计
- **性能优化**: 针对高并发场景优化，使用原子操作减少锁竞争

### 9. 高级数据结构（高性能数据结构）
- **双向映射**: `BidiMap`支持键值双向查找，针对高并发优化
- **多值映射**: `MultiMap`支持一个键对应多个值，提供高效的批量操作
- **懒加载列表**: `LazyList`支持延迟元素生成，减少内存占用
- **性能优化**: 使用`ConcurrentHashMap`和原子操作，针对并发场景优化

### 10. 计数器工具（高性能计数）
- **基础计数器**: `Counter`提供高效的原子计数功能
- **多键计数器**: `MultiCounter`支持多个键的独立计数
- **频率计数器**: `FrequencyCounter`支持频率统计和占比计算
- **性能优化**: 使用原子操作，针对高并发场景优化，提供低延迟计数

## 文档

详细文档请参考 [Wiki](wiki/) 目录：

1. [Processor 接口规范](wiki/PROCESSER_INTERFACE.md) - Processor接口和AbstractProcessor抽象类的使用说明
2. [LoggerProvider 日志记录](wiki/LOGGER_PROVIDER.md) - 统一且可扩展的日志记录接口
3. [通用工具类](wiki/COMMON_UTILS.md) - ExceptionUtil和CommonUtil工具类的使用方法
4. [ProcessorManager 处理器管理](wiki/PROCESSER_MANAGER.md) - 处理器注册和管理机制
5. [线程工具类](wiki/THREAD_UTILS.md) - 线程池管理和任务调度功能
6. [事件总线工具](wiki/EVENT_BUS_UTIL.md) - 高性能异步事件发布订阅机制
7. [分布式锁工具](wiki/DISTRIBUTED_LOCK_UTIL.md) - 可重入分布式锁实现
8. [高级数据结构](wiki/ADVANCED_DATA_STRUCTURES.md) - BidiMap、MultiMap等高级数据结构使用说明
9. [计数器工具](wiki/COUNTER_UTIL.md) - 高性能计数器工具使用说明
10. [缓存工具](wiki/CACHE_UTIL.md) - LRU缓存、过期缓存和计算缓存使用说明

## 使用方法

### 添加依赖

在您的 `build.gradle` 文件中添加依赖：

```gradle
dependencies {
    implementation 'space.commandf2:dilemmalibrary:1.0-SNAPSHOT'
}
```

### 示例代码

```java
// 创建日志提供者
LoggerProvider<org.slf5j.Logger> loggerProvider = 
    new LoggerProvider.LombokLoggerProvider(LoggerFactory.getLogger("MyApp"));

// 创建处理器
Processer<InputStream, MyConfig> yamlProcessor = 
    new YamlConfigProcesser<>(MyConfig.class, loggerProvider);

// 注册并执行处理器
ProcesserManager manager = new ProcesserManager();
manager.registerProcesser(yamlProcessor);
MyConfig config = manager.executeProcesser("YAML Config Processor", inputStream, loggerProvider);
```

## 贡献

欢迎提交Issue和Pull Request来改进这个库。