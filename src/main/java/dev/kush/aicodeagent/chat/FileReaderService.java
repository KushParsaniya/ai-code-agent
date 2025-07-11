package dev.kush.aicodeagent.chat;

import dev.kush.aicodeagent.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileReaderService {

    /**
     * Reads all files from a specific directory
     *
     * @param directoryPath the directory to read from
     * @return Map containing file paths as keys and their content as values
     */
    public Map<String, String> readAllFilesFromDirectory(String directoryPath) {
        try {
            Path targetPath = Paths.get(directoryPath);
            if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
                throw new IllegalArgumentException("Directory does not exist: " + directoryPath);
            }

            return Files.walk(targetPath)
                    .filter(Files::isRegularFile)
                    .filter(FileUtils::isReadableTextFile)
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
    public List<FileInfo> getFileStructure(String directoryPath) {
        try {
            Path currentPath = Paths.get(directoryPath);
            return Files.walk(currentPath)
                    .filter(Files::isRegularFile)
                    .map(path -> createFileInfo(path, currentPath))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file structure", e);
        }
    }

    public List<FileInfo> getFileStructureFromDirectory(String directoryPath) {
        try {
            Path targetPath = Paths.get(directoryPath);
            if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
                throw new IllegalArgumentException("Directory does not exist: " + directoryPath);
            }

            return Files.walk(targetPath)
                    .filter(Files::isRegularFile)
                    .map(path -> createFileInfo(path, targetPath))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file structure from directory: " + directoryPath, e);
        }
    }

    /**
     * Reads content of a specific file
     *
     * @param filePath relative path to the file
     * @return file content as string
     */
    public String readFile(String directorPath,String filePath) {
        try {
            Path path = Paths.get(directorPath).resolve(filePath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("File does not exist: " + filePath);
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
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
                    FileUtils.isReadableTextFile(path)
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