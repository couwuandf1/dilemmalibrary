package space.commandf1.dilemmalibrary.test;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import space.commandf1.dilemmalibrary.provider.LoggerProvider;

@Slf4j
public class DilemmaLibraryTest {
    @Test
    public void test() {
        val loggerProvider = new LoggerProvider<>(log) {
            @Override
            public void log(String message) {
                this.getLogger().info(message);
            }
        };

        loggerProvider.log("Hello World!");
    }
}
