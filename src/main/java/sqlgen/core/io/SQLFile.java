package sqlgen.core.io;

public class SQLFile {
    private final String relativePath;
    private final String content;

    public SQLFile(String relativePath, String content) {
        this.relativePath = relativePath;
        this.content = content;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getContent() {
        return content;
    }
}