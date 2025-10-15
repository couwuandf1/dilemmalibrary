package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

public class StringUtil {

    /**
     * 检查字符串是否为空或仅包含空白字符
     *
     * @param str 要检查的字符串
     * @return 如果字符串为null、空或仅包含空白字符则返回true，否则返回false
     */
    public static boolean isBlank(@NotNull String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 将字符串的首字母大写
     *
     * @param str 要处理的字符串
     * @return 首字母大写的字符串
     */
    @NotNull
    public static String capitalize(@NotNull String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * 将字符串转换为驼峰命名法 (例如: hello_world -> helloWorld)
     *
     * @param str 要转换的字符串
     * @return 驼峰命名法的字符串
     */
    @NotNull
    public static String toCamelCase(@NotNull String str) {
        if (isBlank(str)) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : str.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }
}