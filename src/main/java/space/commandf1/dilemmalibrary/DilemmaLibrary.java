package space.commandf1.dilemmalibrary;

import space.commandf1.dilemmalibrary.provider.LoggerProvider;

public class DilemmaLibrary {
    private static DilemmaLibrary instance;
    private LoggerProvider<?> loggerProvider;

    public static DilemmaLibrary getInstance() {
        if (instance == null) {
            instance = new DilemmaLibrary();
        }

        return instance;
    }

    public LoggerProvider<?> getLoggerProvider() {
        return loggerProvider;
    }

    public void setLoggerProvider(LoggerProvider<?> loggerProvider) {
        this.loggerProvider = loggerProvider;
    }
}
