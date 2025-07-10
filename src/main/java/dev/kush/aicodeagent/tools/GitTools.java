package dev.kush.aicodeagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Component()
public class GitTools {

    /**
     * Creates a new Git branch named "agent" if it doesn't already exist
     * 
     * @return message indicating the result of the operation
     */
    @Tool(name = "createAgentBranch", description = "Creates a new Git branch named 'agent' if it doesn't already exist")
    public String createAgentBranch() {
        try {
            // Check if the branch already exists
            if (branchExists("agent")) {
                return "Branch 'agent' already exists. No changes made.";
            }
            
            // Create the branch
            ProcessBuilder processBuilder = new ProcessBuilder("git", "checkout", "-b", "agent");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                if (process.waitFor(30, TimeUnit.SECONDS)) {
                    if (process.exitValue() == 0) {
                        return "Successfully created and switched to branch 'agent'";
                    } else {
                        return "Failed to create branch 'agent': " + output.toString();
                    }
                } else {
                    process.destroyForcibly();
                    return "Command timed out when creating branch 'agent'";
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error creating branch 'agent': " + e.getMessage();
        }
    }
    
    /**
     * Checks if a Git branch exists
     * 
     * @param branchName name of the branch to check
     * @return true if the branch exists, false otherwise
     */
    private boolean branchExists(String branchName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "branch", "--list", branchName);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            if (process.waitFor(30, TimeUnit.SECONDS)) {
                // If the branch exists, the output will contain the branch name
                return output.toString().trim().contains(branchName);
            } else {
                process.destroyForcibly();
                throw new IOException("Command timed out when checking branch existence");
            }
        }
    }
}