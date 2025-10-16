# EventBusUtil - 高性能事件总线工具

## 简介

EventBusUtil 是一个高性能的事件总线工具类，提供异步事件发布和订阅功能。它针对嵌入式系统进行了优化，确保在资源受限环境下的低内存占用和高并发性能。

## 主要特性

- 异步事件处理
- 支持多种事件类型
- 高并发性能
- 线程安全
- 性能监控功能
- 资源优化

## 使用方法

### 1. 创建 EventBusUtil 实例

```java
// 创建默认实例
EventBusUtil eventBus = new EventBusUtil();

// 创建指定线程池大小的实例
EventBusUtil eventBus = new EventBusUtil(8, 1000);
```

### 2. 定义事件类

```java
public class UserLoginEvent {
    private String username;
    
    public UserLoginEvent(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}
```

### 3. 订阅事件

```java
public class UserService {
    public void onUserLogin(UserLoginEvent event) {
        System.out.println("用户登录: " + event.getUsername());
    }
}

// 订阅事件
UserService userService = new UserService();
eventBus.subscribe(UserLoginEvent.class, userService, "onUserLogin");
```

### 4. 发布事件

```java
// 发布事件
UserLoginEvent event = new UserLoginEvent("张三");
eventBus.publish(event);
```

### 5. 取消订阅

```java
// 取消订阅
eventBus.unsubscribe(UserLoginEvent.class, userService);
```

### 6. 关闭事件总线

```java
// 关闭事件总线
eventBus.shutdown();
```

## 性能监控

EventBusUtil 提供了性能监控功能：

```java
// 获取总事件数
long total = eventBus.getTotalEvents();

// 获取已处理事件数
long processed = eventBus.getProcessedEvents();

// 获取失败事件数
long failed = eventBus.getFailedEvents();

// 计算事件处理成功率
double successRate = eventBus.getSuccessRate();
```

## 最佳实践

1. 在应用程序关闭时记得调用 `shutdown()` 方法释放资源
2. 事件处理方法应该尽快执行完毕，避免长时间阻塞
3. 如果需要处理耗时操作，建议在事件处理方法中再次异步处理
4. 合理设置线程池大小和队列大小以平衡性能和资源消耗