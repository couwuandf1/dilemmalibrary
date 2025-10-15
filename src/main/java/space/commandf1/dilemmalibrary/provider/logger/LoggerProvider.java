package space.commandf1.dilemmalibrary.provider.logger;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import space.commandf1.dilemmalibrary.provider.IProvider;

@EqualsAndHashCode
@ToString
public abstract class LoggerProvider<T> implements IProvider<T> {

    private final T logger;

    public LoggerProvider(@NotNull T logger) {
        this.logger = logger;
    }

    public abstract void log(String message);

    @Override
    public @NotNull T get() {
        return this.logger;
    }

    public static class LombokLoggerProvider extends LoggerProvider<org.slf4j.Logger> {
        public LombokLoggerProvider(org.slf4j.@NotNull Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            this.get().info(message);
        }
    }

    public static class DefaultLoggerProvider extends LoggerProvider<java.util.logging.Logger> {

        public DefaultLoggerProvider(java.util.logging.Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            this.get().info(message);
        }
    }
}
