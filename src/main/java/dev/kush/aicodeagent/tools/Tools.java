package dev.kush.aicodeagent.tools;

        public enum Tools {
            FILE_WRITER("fileWriterTools", "file-system", """
                File Writer Tools - Create and modify files and directories
                Use for: Creating files, updating content, managing file system
                Capabilities: Write/append to files, create directories, delete files, create backups
                Best for: Code generation, file manipulation, workspace organization
            """),

            GIT("gitTools", "version-control", """
                Git Tools - Git version control operations
                Use for: Managing Git branches and repositories
                Capabilities: Create branches, check branch existence
                Best for: Version control tasks, code collaboration
            """);

            private final String beanName;
            private final String category;
            private final String description;

            Tools(String beanName, String category, String description) {
                this.beanName = beanName;
                this.category = category;
                this.description = description;
            }

            public String getBeanName() {
                return beanName;
            }

            public String getCategory() {
                return category;
            }

            public String getDescription() {
                return description;
            }

            public boolean isFileSystem() {
                return "file-system".equals(category);
            }

            public boolean isVersionControl() {
                return "version-control".equals(category);
            }

            public static String getAllInfoAsString() {
                StringBuilder sb = new StringBuilder();
                for (Tools tool : Tools.values()) {
                    sb.append(tool.name()).append(":\n")
                            .append("Bean Name: ").append(tool.getBeanName()).append("\n")
                            .append("Category: ").append(tool.getCategory()).append("\n")
                            .append("Description: ").append(tool.getDescription()).append("\n\n");
                }
                return sb.toString();
            }
        }