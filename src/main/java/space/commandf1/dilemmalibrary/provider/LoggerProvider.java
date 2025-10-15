package space.commandf1.dilemmalibrary.provider;

import lombok.Getter;

public abstract class LoggerProvider<T> implements IProvider<T> {

    @Getter
    private final T logger;

    public LoggerProvider(T logger) {
        this.logger = logger;
    }

    public abstract void log(String message);
    
    public void info(String message) {
        log("[INFO] " + message);
    }
    
    public void warn(String message) {
        log("[WARN] " + message);
    }
    
    public void error(String message) {
        log("[ERROR] " + message);
    }
    
    public void debug(String message) {
        log("[DEBUG] " + message);
    }
}
