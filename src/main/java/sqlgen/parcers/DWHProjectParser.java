package sqlgen.parcers;

import sqlgen.config.DBConfig;
import sqlgen.config.ProjectConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.io.SQLFile;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class DWHProjectParser {
    public static List<SQLFile> findTaskFiles(ProjectConfig projectConfig, DBConfig dbConfig, TaskConfig taskConfig) throws IOException {
        Path startDir = Path.of(projectConfig.path(), projectConfig.dwhPath());
        List<SQLFile> list = new LinkedList<>();

        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String filePath = file.toString().replace("\\", "/").toLowerCase();
                String fileName = file.getFileName().toString().toLowerCase();

                if (fileName.endsWith(".sql")
                        && fileName.contains(taskConfig.taskNo().toLowerCase())
                        && filePath.contains("/" + dbConfig.dbType().toLowerCase() + "/databases/" + dbConfig.database().toLowerCase() + "/")) {
                    list.add(new SQLFile(file, startDir));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return list;
    }
}