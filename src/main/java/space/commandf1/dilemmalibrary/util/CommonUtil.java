package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * 通用工具类，提供常用的工具方法
 */
public class CommonUtil {

    /**
     * 检查对象是否为null
     *
     * @param obj 要检查的对象
     * @return 如果对象为null则返回true，否则返回false
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 要检查的对象
     * @return 如果对象不为null则返回true，否则返回false
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 检查集合是否为空
     *
     * @param collection 要检查的集合
     * @return 如果集合为null或空则返回true，否则返回false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 检查集合是否不为空
     *
     * @param collection 要检查的集合
     * @return 如果集合不为null且不为空则返回true，否则返回false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 检查映射是否为空
     *
     * @param map 要检查的映射
     * @return 如果映射为null或空则返回true，否则返回false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 检查映射是否不为空
     *
     * @param map 要检查的映射
     * @return 如果映射不为null且不为空则返回true，否则返回false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 检查数组是否为空
     *
     * @param array 要检查的数组
     * @return 如果数组为null或空则返回true，否则返回false
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 检查数组是否不为空
     *
     * @param array 要检查的数组
     * @return 如果数组不为null且不为空则返回true，否则返回false
     */
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 获取对象的字符串表示形式，如果对象为null则返回默认值
     *
     * @param obj          要转换的对象
     * @param defaultValue 默认值
     * @return 对象的字符串表示形式或默认值
     */
    @NotNull
    public static String toString(Object obj, @NotNull String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }
}