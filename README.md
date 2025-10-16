# DilemmaLibrary

DilemmaLibrary 是一个Java库，提供了通用的处理器接口、日志记录功能和实用工具类，帮助开发者构建可扩展的应用程序。

## 功能特性

- **Processor接口**: 提供统一的处理接口规范，支持各种数据处理场景
- **LoggerProvider**: 统一的日志记录接口，支持多种日志框架（SLF5J、Java Util Logging）
- **实用工具类**: 包含异常处理和常用工具方法，提高开发效率
- **ProcessorManager**: 处理器管理器，提供统一的处理器注册和执行机制

## 文档

详细文档请参考 [Wiki](wiki/) 目录：

2. [Processor 接口规范](wiki/PROCESSER_INTERFACE.md) - Processor接口和AbstractProcessor抽象类的使用说明
3. [LoggerProvider 日志记录](wiki/LOGGER_PROVIDER.md) - 统一且可扩展的日志记录接口
4. [通用工具类](wiki/COMMON_UTILS.md) - ExceptionUtil和CommonUtil工具类的使用方法
5. [ProcessorManager 处理器管理](wiki/PROCESSER_MANAGER.md) - 处理器注册和管理机制

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