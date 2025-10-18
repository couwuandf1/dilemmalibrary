# 高级数据结构文档

DilemmaLibrary提供了多种高性能的高级数据结构，针对并发场景进行了优化。

## BidiMap - 双向映射

BidiMap支持通过键和值进行双向查找，适用于需要反向查找的场景。

### 主要特性
- 支持键值双向查找
- 线程安全实现
- 高并发性能优化
- 内置性能监控

### 使用示例

```java
// 创建双向映射
BidiMap<String, Integer> bidiMap = new BidiMap<>();

// 添加键值对
bidiMap.put("one", 1);
bidiMap.put("two", 2);

// 正向查找
Integer value = bidiMap.getValue("one"); // 返回 1

// 反向查找
String key = bidiMap.getKey(2); // 返回 "two"

// 移除操作
bidiMap.removeByKey("one");
bidiMap.removeByValue(2);
```

### 性能优化
- 使用ConcurrentHashMap实现线程安全
- 无锁化设计，减少并发竞争
- 原子操作保证数据一致性
- 内置计数器支持性能监控

## MultiMap - 多值映射

MultiMap支持一个键对应多个值，适用于一对多关系的场景。

### 主要特性
- 一个键可对应多个值
- 线程安全实现
- 高效的批量操作
- 内置性能监控

### 使用示例

```java
// 创建多值映射
MultiMap<String, String> multiMap = new MultiMap<>();

// 添加键值对
multiMap.put("fruits", "apple");
multiMap.put("fruits", "banana");
multiMap.put("colors", "red");

// 批量添加
List<String> vegetables = Arrays.asList("carrot", "potato");
multiMap.putAll("vegetables", vegetables);

// 获取值集合
Set<String> fruits = multiMap.get("fruits"); // 返回 {"apple", "banana"}

// 检查是否存在
boolean contains = multiMap.contains("fruits", "apple");

// 移除操作
multiMap.remove("fruits", "apple");
multiMap.removeAll("fruits");
```

### 性能优化
- 使用ConcurrentHashMap和ConcurrentSkipListSet实现
- 原子操作保证数据一致性
- 批量操作减少锁竞争
- 内置计数器支持性能监控

## LazyList - 懒加载列表

LazyList支持延迟元素生成，适用于大容量数据但不需要全部加载的场景。

### 主要特性
- 延迟加载元素
- 内存使用优化
- 线程安全实现
- 内置性能监控

### 使用示例

```java
// 创建基础列表
List<String> baseList = Arrays.asList("item1", "item2", "item3");

// 创建懒加载列表
LazyList<String> lazyList = new LazyList<>(baseList, () -> {
    // 元素生成逻辑
    return "generated_item_" + System.currentTimeMillis();
});

// 访问元素
String item1 = lazyList.get(0); // 返回 "item1"
String generatedItem = lazyList.get(10); // 延迟生成新元素

// 获取列表大小
int size = lazyList.size(); // 返回基础列表和已加载元素的总和
```

### 性能优化
- 按需加载减少内存占用
- ConcurrentHashMap缓存已生成元素
- 原子操作保证线程安全
- 内置计数器支持性能监控