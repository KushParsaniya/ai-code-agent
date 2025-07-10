package dev.kush.aicodeagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Component
public class FileWriterTools {

    @Value("${file.current:D:\\spring-boot\\spring-security-demo\\ai-code-agent}")
    private String currentDirectory;

    /**
     * Writes content to a specific file
     *
     * @param filePath relative path to the file
     * @param content content to write to the file
     * @return true if write operation was successful
     */
    @Tool(name = "writeFile", description = "Writes content to a specified file. Creates the file if it doesn't exist.")
    public boolean writeFile(String filePath, String content) {
        try {
            Path path = Path.of(currentDirectory).resolve(filePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }

    /**
     * Appends content to a specific file
     *
     * @param filePath relative path to the file
     * @param content content to append to the file
     * @return true if append operation was successful
     */
    @Tool(name = "appendToFile", description = "Appends content to a specified file. Creates the file if it doesn't exist.")
    public boolean appendToFile(String filePath, String content) {
        try {
            Path path = Path.of(currentDirectory).resolve(filePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error appending to file: " + filePath, e);
        }
    }

    /**
     * Creates a new directory
     *
     * @param directoryPath path to the directory to create
     * @return true if directory creation was successful
     */
    @Tool(name = "createDirectory", description = "Creates a new directory at the specified path.")
    public boolean createDirectory(String directoryPath) {
        try {
            Path path = Path.of(currentDirectory).resolve(directoryPath);
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error creating directory: " + directoryPath, e);
        }
    }

    /**
     * Deletes a file or directory
     *
     * @param path path to the file or directory to delete
     * @return true if deletion was successful
     */
    @Tool(name = "deleteFileOrDirectory", description = "Deletes the specified file or directory.")
    public boolean deleteFileOrDirectory(String path) {
        try {
            Path targetPath = Path.of(currentDirectory).resolve(path);
            if (!Files.exists(targetPath)) {
                return false;
            }

            if (Files.isDirectory(targetPath)) {
                Files.walk(targetPath)
                    .sorted((p1, p2) -> -p1.compareTo(p2))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException("Error deleting path: " + p, e);
                        }
                    });
            } else {
                Files.delete(targetPath);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error deleting path: " + path, e);
        }
    }

    /**
     * Writes multiple files from a map
     *
     * @param fileContents map containing file paths as keys and their content as values
     * @return number of files successfully written
     */
    @Tool(name = "writeMultipleFiles", description = "Writes multiple files from a map of file paths to content.")
    public int writeMultipleFiles(
            @ToolParam(description = "Map of file paths to their content") Map<String, String> fileContents) {
        int successCount = 0;
        for (Map.Entry<String, String> entry : fileContents.entrySet()) {
            try {
                writeFile(entry.getKey(), entry.getValue());
                successCount++;
            } catch (Exception e) {
                System.err.println("Error writing file " + entry.getKey() + ": " + e.getMessage());
            }
        }
        return successCount;
    }

    /**
     * Creates a backup of a file
     *
     * @param filePath path to the file to backup
     * @return path to the backup file
     */
    @Tool(name = "createFileBackup", description = "Creates a backup of the specified file.")
    public String createFileBackup(String filePath) {
        try {
            Path originalPath = Path.of(currentDirectory).resolve(filePath);
            if (!Files.exists(originalPath)) {
                throw new IllegalArgumentException("File does not exist: " + filePath);
            }

            String backupFileName = filePath + "." + System.currentTimeMillis() + ".backup";
            Path backupPath = Path.of(currentDirectory).resolve(backupFileName);

            Files.copy(originalPath, backupPath);
            return backupFileName;
        } catch (IOException e) {
            throw new RuntimeException("Error creating backup of file: " + filePath, e);
        }
    }
}