import sqlgen.SQLFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SQLWriter {
    private final List<SQLFile> sqlFiles;

    public SQLWriter(List<SQLFile> sqlFiles) {
        this.sqlFiles = sqlFiles;
    }

    public void saveAs(String rootFolderPath) {
        for (SQLFile sqlFile : sqlFiles) {
            File targetFile = new File(rootFolderPath, sqlFile.getRelativePath());
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directories: " + parentDir.getAbsolutePath());
                }
            }

            try (FileWriter writer = new FileWriter(targetFile)) {
                writer.write(sqlFile.getContent());
                System.out.println("Saved: " + targetFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to write: " + targetFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
}
