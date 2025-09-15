package sqlgen.core.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SQLFile {
    private final String relativePath;
    private final String content;

    public SQLFile(String relativePath, String content) {
        this.relativePath = relativePath;
        this.content = content;
    }

    public SQLFile(Path file, Path root) throws IOException {
        this.relativePath = root.relativize(file).toString().replace("\\", "/"); // относительный путь от root
        this.content = Files.readString(file); // читаем всё содержимое в String
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return relativePath;
    }
}
