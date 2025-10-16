# DistributedLockUtil - 高性能分布式锁工具

## 简介

DistributedLockUtil 是一个高性能的分布式锁工具类，提供可重入的分布式锁功能。它针对嵌入式系统进行了优化，确保在资源受限环境下的低内存占用和高并发性能。

## 主要特性

- 可重入锁支持
- 超时自动释放
- 高并发性能
- 线程安全
- 性能监控功能
- 资源优化

## 使用方法

### 1. 创建 DistributedLockUtil 实例

```java
// 创建默认实例
DistributedLockUtil lockUtil = new DistributedLockUtil();
```

### 2. 获取分布式锁

```java
// 获取分布式锁
DistributedLockUtil.DistributedLock lock = lockUtil.getLock("resource_key");
```

### 3. 获取锁

```java
// 阻塞获取锁
lock.lock();

// 可中断获取锁
try {
    lock.lockInterruptibly();
} catch (InterruptedException e) {
    // 处理中断
}

// 尝试获取锁
if (lock.tryLock()) {
    try {
        // 执行临界区代码
    } finally {
        lock.unlock();
    }
}

// 尝试获取锁（带超时）
if (lock.tryLock(5000, TimeUnit.MILLISECONDS)) {
    try {
        // 执行临界区代码
    } finally {
        lock.unlock();
    }
}
```

### 4. 设置锁超时

```java
// 设置锁超时时间，防止死锁
lock.setTimeout(30, TimeUnit.SECONDS);
```

### 5. 释放锁

```java
// 释放锁
lock.unlock();
```

### 6. 关闭分布式锁工具

```java
// 关闭分布式锁工具
lockUtil.shutdown();
```

## 性能监控

DistributedLockUtil 提供了性能监控功能：

```java
// 获取总锁请求次数
long total = lockUtil.getTotalLocks();

// 获取成功获取锁的次数
long successful = lockUtil.getSuccessfulLocks();

// 获取失败锁的次数
long failed = lockUtil.getFailedLocks();

// 获取锁超时次数
long timeouts = lockUtil.getLockTimeouts();

// 计算锁获取成功率
double successRate = lockUtil.getSuccessRate();
```

## 最佳实践

1. 在应用程序关闭时记得调用 `shutdown()` 方法释放资源
2. 始终在 finally 块中释放锁，确保锁被正确释放
3. 合理设置锁超时时间，防止死锁
4. 锁的粒度要适中，过粗会影响并发性能，过细会增加系统开销
5. 避免在持有锁的情况下执行耗时操作
6. 使用 tryLock() 方法可以避免线程长时间阻塞