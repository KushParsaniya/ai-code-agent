package dev.kush.aicodeagent.chat;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// index project and store in vector database
@Service
public class IndexingService {

    private final IndexProjectRepository indexProjectRepository;
    @Value("${file.current}")
    private String homeDirectory;

    private final FileReaderService fileReaderService;
    private final VectorStore vectorStore;

    public IndexingService(FileReaderService fileReaderService, VectorStore vectorStore, IndexProjectRepository indexProjectRepository) {
        this.fileReaderService = fileReaderService;
        this.vectorStore = vectorStore;
        this.indexProjectRepository = indexProjectRepository;
    }

    public void indexProject(String projectId) {
        // first index project structure
        if (!indexProjectRepository.isProjectIndexed(projectId)) {
            List<FileReaderService.FileInfo> fileInfos = fileReaderService.getFileStructure(homeDirectory);

            List<Document> documents = fileInfos.stream()
                    .map(fileInfo -> toDocument(fileInfo, Map.of("type", "fileStructure"), projectId))
                    .toList();
            vectorStore.add(documents);

            // now index files
            Map<String, String> fileContents = fileReaderService.readAllFilesFromDirectory(homeDirectory);

            fileContents.entrySet()
                    .stream()
                    .map(entry -> {
                        FileReaderService.FileInfo fileInfo = new FileReaderService.FileInfo(
                                entry.getKey(),
                                entry.getValue().length(),
                                Instant.now().toString(),
                                true,
                                true);
                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("type", "fileContent");
                        return toDocument(fileInfo, entry.getValue(), metadata, projectId);
                    })
                    .map(List::of)
                    .forEach(vectorStore::add);

            IndexProject projectIndex = new IndexProject();
            projectIndex.setProjectId(projectId);
            projectIndex.setProjectPath(homeDirectory);
            indexProjectRepository.save(projectIndex);
        } else {
            System.out.println("Project already indexed: " + projectId);
        }

    }

    private static Document toDocument(FileReaderService.FileInfo fileInfo, Map<String, Object> metadata, String projectId) {
        Map<String, Object> metadataMap = new HashMap<>(metadata);
        metadataMap.put("relativePath", fileInfo.relativePath());
        metadataMap.put("extension", fileInfo.relativePath().substring(fileInfo.relativePath().lastIndexOf('.') + 1));
        metadataMap.put("projectId", projectId.replace("\"", ""));

        return new Document("""
                relativePath: %s
                size: %d
                lastModified: %s
                readable: %b
                """.formatted(fileInfo.relativePath(), fileInfo.size(), fileInfo.lastModified(), fileInfo.readable()), metadataMap);
    }

    private static Document toDocument(FileReaderService.FileInfo fileInfo, String content, Map<String, Object> metadata, String projectId) {
        Map<String, Object> metadataMap = new HashMap<>(metadata);
        metadataMap.put("relativePath", fileInfo.relativePath());
        metadataMap.put("extension", fileInfo.relativePath().substring(fileInfo.relativePath().lastIndexOf('.') + 1));
        metadataMap.put("projectId", projectId.replace("\"", ""));

        return new Document("""
                relativePath: %s
                size: %d
                lastModified: %s
                readable: %b
                content: %s
                """.formatted(fileInfo.relativePath(), fileInfo.size(), fileInfo.lastModified(), fileInfo.readable(), content), metadataMap);
    }


}
