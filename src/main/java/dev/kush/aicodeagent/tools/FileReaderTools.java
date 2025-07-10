package dev.kush.aicodeagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileReaderTools {

    /**
     * Reads all file information from the current working directory
     *
     * @return Map containing file paths as keys and their content as values
     */
    @Value("${file.current:D:\\spring-boot\\spring-security-demo\\ai-code-agent\\src\\main}")
    private String currentDirectory;


    public Map<String, String> readAllFilesFromCurrentFolder() {
        try {
            Path currentPath = Paths.get(currentDirectory);
            return Files.walk(currentPath)
                    .filter(Files::isRegularFile)
                    .filter(this::isReadableTextFile)
                    .collect(Collectors.toMap(
                            path -> currentPath.relativize(path).toString(),
                            this::readFileContent
                    ));
        } catch (IOException e) {
            throw new RuntimeException("Error reading files from current folder", e);
        }
    }

    /**
     * Reads all files from a specific directory
     *
     * @param directoryPath the directory to read from
     * @return Map containing file paths as keys and their content as values
     */
    @Tool(name = "readAllFilesFromDirectory", description = "Reads all files from a specified directory and returns their content.")
    public Map<String, String> readAllFilesFromDirectory(String directoryPath) {
        try {
            Path targetPath = Paths.get(directoryPath);
            if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
                throw new IllegalArgumentException("Directory does not exist: " + directoryPath);
            }

            return Files.walk(targetPath)
                    .filter(Files::isRegularFile)
                    .filter(this::isReadableTextFile)
                    .collect(Collectors.toMap(
                            path -> targetPath.relativize(path).toString(),
                            this::readFileContent
                    ));
        } catch (IOException e) {
            throw new RuntimeException("Error reading files from directory: " + directoryPath, e);
        }
    }

    /**
     * Gets file structure information without reading content
     *
     * @return List of file information objects
     */
    @Tool(name = "getFileStructure", description = "Retrieves the file structure of the current working directory without reading file content.")
    public List<FileInfo> getFileStructure() {
        try {
            Path currentPath = Paths.get(currentDirectory);
            return Files.walk(currentPath)
                    .filter(Files::isRegularFile)
                    .map(path -> createFileInfo(path, currentPath))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file structure", e);
        }
    }

    /**
     * Reads content of a specific file
     *
     * @param filePath relative path to the file
     * @return file content as string
     */
    @Tool(name = "readFile", description = "Reads the content of a specified file.")
    public String readFile(String filePath) {
        try {
            Path path = Paths.get(currentDirectory).resolve(filePath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("File does not exist: " + filePath);
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    private boolean isReadableTextFile(Path path) {
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
                fileName.equals("dockerfile") ||
                !fileName.contains(".");
    }

    private String readFileContent(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private FileInfo createFileInfo(Path path, Path basePath) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return new FileInfo(
                    basePath.relativize(path).toString(),
                    attrs.size(),
                    attrs.lastModifiedTime().toString(),
                    Files.isReadable(path),
                    isReadableTextFile(path)
            );
        } catch (IOException e) {
            return new FileInfo(
                    basePath.relativize(path).toString(),
                    0L,
                    "unknown",
                    false,
                    false
            );
        }
    }

    public record FileInfo(
            String relativePath,
            long size,
            String lastModified,
            boolean readable,
            boolean isTextFile
    ) {
    }
}