package space.commandf1.dilemmalibrary.processer;

public abstract class CacheProcesser<T> implements IProcessAdapter {
    private T cache;
    private final long cacheTimeout; // 缓存超时时间（毫秒）
    private long lastUpdateTime; // 上次更新时间

    public CacheProcesser(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public T getCache() {
        if (isCacheExpired()) {
            cache = loadCache();
            lastUpdateTime = System.currentTimeMillis();
        }
        return cache;
    }

    public void clearCache() {
        this.cache = null;
        this.lastUpdateTime = 0;
    }

    public boolean isCacheExpired() {
        return System.currentTimeMillis() - lastUpdateTime > cacheTimeout;
    }

    protected abstract T loadCache();

    @Override
    public void process() {
        getCache();
    }
}