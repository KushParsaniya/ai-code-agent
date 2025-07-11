package dev.kush.aicodeagent.utils;

import java.nio.file.Path;

public class FileUtils {

    public static boolean isReadableTextFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".java") ||
                fileName.endsWith(".xml") ||
                fileName.endsWith(".properties") ||
                fileName.endsWith(".yml") ||
                fileName.endsWith(".yaml") ||
                fileName.endsWith(".json") ||
                fileName.endsWith(".md") ||
                fileName.endsWith(".txt") ||
                fileName.endsWith(".gradle") ||
                fileName.equalsIgnoreCase("dockerfile") ||
                !fileName.contains(".");
    }
}
