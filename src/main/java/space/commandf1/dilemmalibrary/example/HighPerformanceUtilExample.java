package space.commandf1.dilemmalibrary.example;

import space.commandf1.dilemmalibrary.util.CacheUtil;
import space.commandf1.dilemmalibrary.util.DistributedLockUtil;
import space.commandf1.dilemmalibrary.util.EventBusUtil;

import java.util.concurrent.TimeUnit;

/**
 * 高性能工具类使用示例
 */
public class HighPerformanceUtilExample {
    
    public static void demonstrateCacheUtil() {
        System.out.println("=== CacheUtil 示例 ===");
        
        // LRU缓存示例
        CacheUtil.LRUCache<String, String> lruCache = new CacheUtil.LRUCache<>(3);
        lruCache.put("key1", "value1");
        lruCache.put("key2", "value2");
        lruCache.put("key3", "value3");
        
        System.out.println("LRU缓存大小: " + lruCache.size());
        System.out.println("获取key1: " + lruCache.get("key1"));
        
        // 添加第四个元素，会淘汰最久未使用的key1
        lruCache.put("key4", "value4");
        System.out.println("添加key4后，获取key1: " + lruCache.get("key1")); // 应该返回null
        System.out.println("LRU缓存命中率: " + lruCache.getHitRate());
        
        // 带过期时间的缓存示例
        CacheUtil.ExpiringCache<String, String> expiringCache = new CacheUtil.ExpiringCache<>();
        expiringCache.put("tempKey", "tempValue", 1000); // 1秒后过期
        System.out.println("过期缓存值: " + expiringCache.get("tempKey"));
        
        try {
            Thread.sleep(1100); // 等待过期
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("过期后获取: " + expiringCache.get("tempKey")); // 应该返回null
        
        // 计算缓存示例
        CacheUtil.ComputingCache<String, String> computingCache = new CacheUtil.ComputingCache<>(key -> {
            System.out.println("计算值 for key: " + key);
            return "computed_" + key;
        });
        
        System.out.println("计算缓存获取key1: " + computingCache.get("key1"));
        System.out.println("再次获取key1: " + computingCache.get("key1")); // 不会重新计算
        
        // 关闭过期缓存的调度器
        expiringCache.shutdown();
    }
    
    public static void demonstrateEventBusUtil() {
        System.out.println("\n=== EventBusUtil 示例 ===");
        
        EventBusUtil eventBus = new EventBusUtil();
        
        // 创建事件监听器
        class UserEventListener {
            public void onUserLogin(UserLoginEvent event) {
                System.out.println("用户登录事件: " + event.getUsername());
            }
            
            public void onUserLogout(UserLogoutEvent event) {
                System.out.println("用户登出事件: " + event.getUsername());
            }
        }
        
        UserEventListener listener = new UserEventListener();
        
        // 订阅事件
        eventBus.subscribe(UserLoginEvent.class, listener, "onUserLogin");
        eventBus.subscribe(UserLogoutEvent.class, listener, "onUserLogout");
        
        // 发布事件
        eventBus.publish(new UserLoginEvent("张三"));
        eventBus.publish(new UserLogoutEvent("张三"));
        eventBus.publish(new UserLoginEvent("李四"));
        
        // 等待事件处理完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("总事件数: " + eventBus.getTotalEvents());
        System.out.println("已处理事件数: " + eventBus.getProcessedEvents());
        System.out.println("事件处理成功率: " + eventBus.getSuccessRate());
        
        // 关闭事件总线
        eventBus.shutdown();
    }
    
    public static void demonstrateDistributedLockUtil() {
        System.out.println("\n=== DistributedLockUtil 示例 ===");
        
        DistributedLockUtil lockUtil = new DistributedLockUtil();
        
        // 获取锁
        DistributedLockUtil.DistributedLock lock = lockUtil.getLock("shared_resource");
        
        // 尝试获取锁
        if (lock.tryLock()) {
            try {
                System.out.println("成功获取锁，执行临界区代码");
                
                // 设置锁超时时间
                lock.setTimeout(5, TimeUnit.SECONDS);
                
                // 模拟执行一些操作
                Thread.sleep(100);
                
                System.out.println("临界区代码执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println("锁已释放");
            }
        } else {
            System.out.println("获取锁失败");
        }
        
        // 阻塞获取锁
        try {
            lock.lock();
            try {
                System.out.println("阻塞获取锁成功，执行临界区代码");
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println("锁已释放");
            }
        } finally {
            // 关闭锁工具
            lockUtil.shutdown();
        }
        
        System.out.println("总锁请求次数: " + lockUtil.getTotalLocks());
        System.out.println("成功获取锁次数: " + lockUtil.getSuccessfulLocks());
        System.out.println("锁获取成功率: " + lockUtil.getSuccessRate());
    }
    
    // 示例事件类
    public static class UserLoginEvent {
        private final String username;
        
        public UserLoginEvent(String username) {
            this.username = username;
        }
        
        public String getUsername() {
            return username;
        }
    }
    
    public static class UserLogoutEvent {
        private final String username;
        
        public UserLogoutEvent(String username) {
            this.username = username;
        }
        
        public String getUsername() {
            return username;
        }
    }
    
    public static void main(String[] args) {
        demonstrateCacheUtil();
        demonstrateEventBusUtil();
        demonstrateDistributedLockUtil();
    }
}