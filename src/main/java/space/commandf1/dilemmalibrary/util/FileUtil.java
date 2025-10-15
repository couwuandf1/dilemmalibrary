package space.commandf1.dilemmalibrary.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    /**
     * 读取文件的所有行
     *
     * @param filePath 文件路径
     * @return 文件内容的行列表
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static List<String> readAllLines(@NotNull String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果文件存在则返回true，否则返回false
     */
    public static boolean exists(@NotNull String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 创建目录（包括必要的父目录）
     *
     * @param dirPath 目录路径
     * @return 如果目录创建成功或已存在则返回true，否则返回false
     */
    public static boolean createDirectories(@NotNull String dirPath) {
        Path path = Paths.get(dirPath);
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}