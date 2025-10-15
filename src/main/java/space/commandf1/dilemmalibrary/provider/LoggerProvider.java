package space.commandf1.dilemmalibrary.provider;

import lombok.Getter;

public abstract class LoggerProvider<T> implements IProvider<T> {

    @Getter
    private final T logger;

    public LoggerProvider(T logger) {
        this.logger = logger;
    }

    public abstract void log(String message);
}
