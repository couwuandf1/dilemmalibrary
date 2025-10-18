# 计数器工具文档

DilemmaLibrary提供了多种高性能的计数器工具，针对并发场景进行了优化。

## Counter - 基础计数器

Counter提供基本的计数功能，支持原子操作。

### 主要特性
- 原子操作保证线程安全
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建计数器
Counter counter = new Counter();

// 基本操作
int value1 = counter.increment(); // 增加并返回新值
int value2 = counter.decrement(); // 减少并返回新值
int value3 = counter.add(5);      // 增加指定值并返回新值

// 获取当前值
int current = counter.get();

// 重置计数器
int previous = counter.reset(0);

// 原子比较并设置
boolean success = counter.compareAndSet(0, 10);
```

### 性能优化
- 使用AtomicInteger实现原子操作
- 无锁化设计，减少并发竞争
- 内置操作计数器支持性能监控

## MultiCounter - 多键计数器

MultiCounter支持多个键的独立计数，适用于需要分类统计的场景。

### 主要特性
- 多键独立计数
- 线程安全实现
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建多键计数器
MultiCounter<String> multiCounter = new MultiCounter<>();

// 基本操作
int count1 = multiCounter.increment("key1");     // 增加指定键的计数
int count2 = multiCounter.decrement("key2");     // 减少指定键的计数
int count3 = multiCounter.add("key1", 5);        // 增加指定键的指定值

// 获取计数
int current = multiCounter.get("key1");

// 重置计数
int previous = multiCounter.reset("key1", 0);

// 移除键
int removed = multiCounter.remove("key1");

// 获取总计数
int total = multiCounter.getTotalCount();

// 获取键数量
int size = multiCounter.size();
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- 原子操作保证数据一致性
- 内置计数器支持性能监控

## FrequencyCounter - 频率计数器

FrequencyCounter支持频率统计和占比计算，适用于需要频率分析的场景。

### 主要特性
- 频率统计
- 占比计算
- 线程安全实现
- 高并发性能优化

### 使用示例

```java
// 创建频率计数器
FrequencyCounter<String> frequencyCounter = new FrequencyCounter<>();

// 增加频率
int freq1 = frequencyCounter.increment("item1");
int freq2 = frequencyCounter.add("item2", 3);    // 批量增加

// 获取频率
int frequency = frequencyCounter.getFrequency("item1");

// 获取频率占比
double ratio = frequencyCounter.getFrequencyRatio("item1");

// 获取总计数
int total = frequencyCounter.getTotalCount();

// 获取所有键
Set<String> keys = frequencyCounter.keySet();

// 移除键
int removed = frequencyCounter.remove("item1");
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- 原子操作保证数据一致性
- 高效的频率计算算法
- 内置计数器支持性能监控