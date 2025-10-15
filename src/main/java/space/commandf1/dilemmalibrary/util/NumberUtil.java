package space.commandf1.dilemmalibrary.util;

public class NumberUtil {
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }
    
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static int safeParseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static double safeParseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}