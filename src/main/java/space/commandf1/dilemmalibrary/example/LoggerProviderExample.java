package space.commandf1.dilemmalibrary.example;

import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class LoggerProviderExample {
    public static void main(String[] args) {
        // 测试 SLF4J 日志提供者
        org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger("SLF4JExample");
        LoggerProvider.LombokLoggerProvider slf4jProvider = new LoggerProvider.LombokLoggerProvider(slf4jLogger);
        
        System.out.println("Testing SLF4J Logger Provider:");
        slf4jProvider.log("This is a basic log message");
        slf4jProvider.debug("This is a debug message");
        slf4jProvider.info("This is an info message");
        slf4jProvider.warn("This is a warning message");
        slf4jProvider.error("This is an error message");
        
        Exception testException = new Exception("Test exception");
        slf4jProvider.log("Log with exception", testException);
        slf4jProvider.debug("Debug with exception", testException);
        slf4jProvider.info("Info with exception", testException);
        slf4jProvider.warn("Warn with exception", testException);
        slf4jProvider.error("Error with exception", testException);
        
        // 测试 Java Util Logging 日志提供者
        Logger defaultLogger = Logger.getLogger("DefaultExample");
        LoggerProvider.DefaultLoggerProvider defaultProvider = new LoggerProvider.DefaultLoggerProvider(defaultLogger);
        
        System.out.println("\nTesting Java Util Logging Provider:");
        defaultProvider.log("This is a basic log message");
        defaultProvider.debug("This is a debug message");
        defaultProvider.info("This is an info message");
        defaultProvider.warn("This is a warning message");
        defaultProvider.error("This is an error message");
        
        defaultProvider.log("Log with exception", testException);
        defaultProvider.debug("Debug with exception", testException);
        defaultProvider.info("Info with exception", testException);
        defaultProvider.warn("Warn with exception", testException);
        defaultProvider.error("Error with exception", testException);
        
        System.out.println("\nExample completed successfully!");
    }
}