package space.commandf1.dilemmalibrary;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.util.HashSet;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class DilemmaLibraryHandler {
    private final Set<LoggerProvider<?>> loggers = new HashSet<>();

    private DilemmaLibraryHandler() {
    }

    public <T> void registerLoggerProvider(LoggerProvider<T> provider) {
        this.loggers.add(provider);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final DilemmaLibraryHandler handler;

        private Builder() {
            this.handler = new DilemmaLibraryHandler();
        }

        public <T> Builder loggerProvider(LoggerProvider<T> provider) {
            this.handler.registerLoggerProvider(provider);
            return this;
        }

        public DilemmaLibraryHandler build() {
            return this.handler;
        }
    }
}
