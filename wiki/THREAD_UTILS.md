# 线程工具类文档

提供了丰富的线程处理工具类，包括`ThreadUtil`、`TaskScheduler`和`ThreadPoolMonitor`，用于简化多线程编程并提供高性能的线程管理功能。

## ThreadUtil 线程工具类

```java
public class ThreadUtil {
    // 线程池创建方法
    public static ExecutorService createFixedThreadPool(int size);
    public static ExecutorService createCachedThreadPool();
    public static ExecutorService createSingleThreadExecutor();
    public static ScheduledExecutorService createScheduledThreadPool(int corePoolSize);
    public static ExecutorService createOptimizedFixedThreadPool(int size, int queueSize);
    
    // 线程池管理方法
    public static void shutdown(ExecutorService executor);
    public static void shutdownNow(ExecutorService executor);
    public static boolean awaitTermination(ExecutorService executor, long timeout, TimeUnit unit);
    
    // 任务提交和执行方法
    public static <T> Future<T> submit(ExecutorService executor, Callable<T> task);
    public static <T> Future<T> submit(ExecutorService executor, Supplier<T> task);
    public static void execute(ExecutorService executor, Runnable task);
    
    // 定时任务方法
    public static ScheduledFuture<?> schedule(ScheduledExecutorService executor, Runnable task, long delay, TimeUnit unit);
    public static ScheduledFuture<?> scheduleAtFixedRate(ScheduledExecutorService executor, Runnable task, long initialDelay, long period, TimeUnit unit);
    public static ScheduledFuture<?> scheduleWithFixedDelay(ScheduledExecutorService executor, Runnable task, long initialDelay, long delay, TimeUnit unit);
}
```

### 方法说明

#### 线程池创建方法
- `createFixedThreadPool(int size)`: 创建一个固定大小的线程池
- `createCachedThreadPool()`: 创建一个可缓存的线程池
- `createSingleThreadExecutor()`: 创建一个单线程的线程池
- `createScheduledThreadPool(int corePoolSize)`: 创建一个定时任务线程池
- `createOptimizedFixedThreadPool(int size, int queueSize)`: 创建一个优化的固定大小线程池，使用自定义队列大小

#### 线程池管理方法
- `shutdown(ExecutorService executor)`: 关闭线程池
- `shutdownNow(ExecutorService executor)`: 立即关闭线程池
- `awaitTermination(ExecutorService executor, long timeout, TimeUnit unit)`: 等待所有任务完成

#### 任务提交和执行方法
- `submit(...)`: 提交一个任务并返回结果
- `execute(ExecutorService executor, Runnable task)`: 执行一个任务

#### 定时任务方法
- `schedule(...)`: 在指定延迟后执行任务
- `scheduleAtFixedRate(...)`: 以固定频率执行任务
- `scheduleWithFixedDelay(...)`: 以固定延迟执行任务

### 使用示例

```java
// 创建固定大小线程池
ExecutorService fixedThreadPool = ThreadUtil.createFixedThreadPool(4);

// 提交任务
Future<String> future = ThreadUtil.submit(fixedThreadPool, () -> {
    // 执行一些工作
    return "Task completed";
});

// 获取结果
try {
    String result = future.get();
    System.out.println(result);
} catch (Exception e) {
    e.printStackTrace();
}

// 关闭线程池
ThreadUtil.shutdown(fixedThreadPool);
```

## TaskScheduler 任务调度器

```java
public class TaskScheduler {
    public TaskScheduler();
    public TaskScheduler(int corePoolSize);
    
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit);
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit);
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit);
    
    public void shutdown();
    public void shutdownNow();
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
}
```

### 方法说明

- `schedule(...)`: 在指定延迟后执行任务
- `scheduleAtFixedRate(...)`: 以固定频率执行任务
- `scheduleWithFixedDelay(...)`: 以固定延迟执行任务
- `shutdown()`: 关闭调度器
- `shutdownNow()`: 立即关闭调度器
- `awaitTermination(...)`: 等待调度器终止

### 使用示例

```java
// 创建任务调度器
TaskScheduler scheduler = new TaskScheduler();

// 定时执行任务
scheduler.schedule(() -> {
    System.out.println("定时任务执行");
}, 5, TimeUnit.SECONDS);

// 以固定频率执行任务
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("固定频率任务执行");
}, 0, 10, TimeUnit.SECONDS);

// 关闭调度器
scheduler.shutdown();
```

## ThreadPoolMonitor 线程池监控器

```java
public class ThreadPoolMonitor {
    public ThreadPoolMonitor(ThreadPoolExecutor executor, String poolName);
    public void shutdown();
    public ThreadPoolExecutor getExecutor();
}
```

### 方法说明

- `ThreadPoolMonitor(ThreadPoolExecutor executor, String poolName)`: 构造函数，创建线程池监控器
- `shutdown()`: 关闭监控
- `getExecutor()`: 获取线程池实例

### 使用示例

```java
// 创建线程池
ThreadPoolExecutor executor = (ThreadPoolExecutor) ThreadUtil.createFixedThreadPool(4);

// 创建监控器
ThreadPoolMonitor monitor = new ThreadPoolMonitor(executor, "MyThreadPool");

// 提交一些任务
for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        // 执行一些工作
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
}

// 关闭监控
monitor.shutdown();
```

### 监控输出示例

```
[INFO] 线程池 [MyThreadPool] 状态: 活跃线程数=4, 核心线程数=4, 最大线程数=4, 队列大小=6, 已完成任务数=0, 总任务数=10
[INFO] 线程池 [MyThreadPool] 状态: 活跃线程数=4, 核心线程数=4, 最大线程数=4, 队列大小=2, 已完成任务数=4, 总任务数=10
[INFO] 线程池 [MyThreadPool] 状态: 活跃线程数=2, 核心线程数=4, 最大线程数=4, 队列大小=0, 已完成任务数=8, 总任务数=10
[INFO] 线程池 [MyThreadPool] 状态: 活跃线程数=0, 核心线程数=4, 最大线程数=4, 队列大小=0, 已完成任务数=10, 总任务数=10
```