package sqlgenlib.parcers;

import sqlgenlib.config.DBConfig;
import sqlgenlib.config.ProjectConfig;
import sqlgenlib.core.io.SQLFile;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MigrationFilesSearcher {
    public ProjectConfig getProjectConfig() {
        return projectConfig;
    }

    private final ProjectConfig projectConfig;
    private final DBConfig dbConfig;

    public MigrationFilesSearcher(ProjectConfig projectConfig, DBConfig dbConfig) {
        this.projectConfig = projectConfig;
        this.dbConfig = dbConfig;
    }

    public List<SQLFile> getSQLFiles(String schemeCode, String tableCode) throws IOException {
        List<SQLFile> sqlFiles = new LinkedList<>();
        Path startDir = Path.of(projectConfig.path(), projectConfig.dwhPath(), fillTemplate(getPathTemplate(), dbConfig, schemeCode, tableCode));
        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".sql")) {
                    sqlFiles.add(new SQLFile(file, startDir));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return sqlFiles;
    }

    public List<String> getTableNames(String schemaCode) {
        Path startDir = Path.of(projectConfig.path(), projectConfig.dwhPath(), fillTemplate(getPathTemplate(), dbConfig, schemaCode, ""));

        try {
            return Files.list(startDir)
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // Обработка ошибок - можно вернуть пустой список или выбросить исключение
            System.err.println("Ошибка при чтении директории: " + startDir + " - " + e.getMessage());
            return List.of(); // возвращаем пустой список в случае ошибки
        }
    }

    public String getPathTemplate() {
        return projectConfig.tableMigrationPathTemplate();
    }

    protected static String fillTemplate(
            String template,
            DBConfig dbConfig,
            String schemeCode,
            String tableCode
        ) {
        return template
                .replace("{dbType}", dbConfig.dbType())
                .replace("{database}", dbConfig.database())
                .replace("{scheme}", schemeCode)
                .replace("{table}", tableCode);
    }
}
