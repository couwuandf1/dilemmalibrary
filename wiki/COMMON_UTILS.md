# 通用工具类文档

提供了两个通用工具类：`ExceptionUtil` 用于异常处理，`CommonUtil` 提供常用的工具方法。

## ExceptionUtil 异常处理工具类

```java
public class ExceptionUtil {
    public static @NotNull String getStackTrace(@NotNull Throwable throwable);
    public static void printStackTrace(@NotNull Throwable throwable);
}
```

### 方法说明

- `getStackTrace(@NotNull Throwable throwable)`: 将异常堆栈信息转换为字符串返回。
- `printStackTrace(@NotNull Throwable throwable)`: 将异常堆栈信息打印到标准错误输出。

### 使用示例

```java
try {
    // some code that may throw exception
} catch (Exception e) {
    // 获取堆栈信息字符串
    String stackTrace = ExceptionUtil.getStackTrace(e);
    System.out.println(stackTrace);
    
    // 或者直接打印堆栈信息
    ExceptionUtil.printStackTrace(e);
}
```

## CommonUtil 通用工具类

```java
public class CommonUtil {
    public static boolean isNull(Object obj);
    public static boolean isNotNull(Object obj);
    public static boolean isEmpty(Collection<?> collection);
    public static boolean isNotEmpty(Collection<?> collection);
    public static boolean isEmpty(Map<?, ?> map);
    public static boolean isNotEmpty(Map<?, ?> map);
    public static boolean isEmpty(Object[] array);
    public static boolean isNotEmpty(Object[] array);
    public static @NotNull String toString(Object obj, @NotNull String defaultValue);
}
```

### 方法说明

- `isNull(Object obj)` / `isNotNull(Object obj)`: 检查对象是否为 null 或不为 null。
- `isEmpty(...)`: 检查集合、映射或数组是否为空（null 或无元素）。
- `isNotEmpty(...)`: 检查集合、映射或数组是否不为空。
- `toString(Object obj, @NotNull String defaultValue)`: 安全地将对象转换为字符串，如果对象为 null 则返回默认值。

### 使用示例

```java
// 对象检查
if (CommonUtil.isNotNull(someObject)) {
    // 处理非空对象
}

// 集合检查
List<String> list = getList();
if (CommonUtil.isNotEmpty(list)) {
    // 处理非空列表
}

// 安全的 toString
String value = CommonUtil.toString(someObject, "default");
```