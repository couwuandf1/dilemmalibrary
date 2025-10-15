package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ArrayUtil {
    public static <T> boolean contains(@NotNull T[] array, @NotNull T element) {
        for (final T current : array) {
            if (element.equals(current)) {
                return true;
            }
        }

        return false;
    }
}
