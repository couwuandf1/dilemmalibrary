# 高性能缓存系统文档

DilemmaLibrary提供了多种高性能的缓存实现，针对并发场景进行了优化。

## LRUCache - LRU缓存

LRUCache实现最近最少使用算法，适用于需要控制缓存大小的场景。

### 主要特性
- LRU算法实现
- 线程安全实现
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建LRU缓存，最大大小为100
LRUCache<String, String> lruCache = new LRUCache<>(100);

// 添加缓存项
lruCache.put("key1", "value1");
lruCache.put("key2", "value2");

// 获取缓存项
String value = lruCache.get("key1");

// 移除缓存项
String removed = lruCache.remove("key1");

// 获取缓存大小
int size = lruCache.size();

// 获取命中率
double hitRate = lruCache.getHitRate();
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- LinkedBlockingDeque维护访问顺序
- 无锁化设计，减少并发竞争
- 内置计数器支持性能监控

## ExpiringCache - 过期缓存

ExpiringCache支持自动过期机制，防止内存泄漏，适用于需要自动清理过期数据的场景。

### 主要特性
- 自动过期机制
- 线程安全实现
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建过期缓存
ExpiringCache<String, String> expiringCache = new ExpiringCache<>();

// 添加缓存项，5秒后过期
expiringCache.put("key1", "value1", 5000);

// 获取缓存项
String value = expiringCache.get("key1");

// 移除缓存项
String removed = expiringCache.remove("key1");

// 获取缓存大小
int size = expiringCache.size();

// 获取命中率
double hitRate = expiringCache.getHitRate();

// 关闭缓存
expiringCache.shutdown();
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- ScheduledExecutorService处理过期任务
- 原子操作保证数据一致性
- 内置计数器支持性能监控

## ComputingCache - 计算缓存

ComputingCache支持自动加载和缓存穿透防护，适用于需要根据键自动计算值的场景。

### 主要特性
- 自动加载机制
- 缓存穿透防护
- 线程安全实现
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建计算缓存
ComputingCache<String, String> computingCache = new ComputingCache<>(key -> {
    // 模拟耗时计算
    return "computed_" + key;
});

// 获取缓存项，如果不存在则自动加载
String value = computingCache.get("key1");

// 手动添加缓存项
computingCache.put("key2", "value2");

// 移除缓存项
String removed = computingCache.remove("key1");

// 获取缓存大小
int size = computingCache.size();

// 获取命中率
double hitRate = computingCache.getHitRate();

// 获取加载次数
long loadCount = computingCache.getLoadCount();
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- computeIfAbsent防止并发加载同一键
- 原子操作保证数据一致性
- 内置计数器支持性能监控