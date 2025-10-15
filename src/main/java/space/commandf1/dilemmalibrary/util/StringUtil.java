package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

public class StringUtil {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static String capitalize(@NotNull String str) {
        if (str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}