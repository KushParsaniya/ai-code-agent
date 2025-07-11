package dev.kush.aicodeagent.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexProjectRepository extends JpaRepository<IndexProject, Long> {

    @Query("SELECT COUNT(1) > 0 FROM IndexProject i WHERE i.projectId = ?1")
    boolean isProjectIndexed(String projectId);
}