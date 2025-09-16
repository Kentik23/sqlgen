package sqlgenlib.parcers;

import sqlgenlib.config.DBConfig;
import sqlgenlib.config.ProjectConfig;
import sqlgenlib.core.io.SQLFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChangelogParser {
    protected static String buildChangeLogPath(String template, String dbType, String database) {
        return template
                .replace("{dbType}", dbType)
                .replace("{database}", database);
    }

    public static SQLFile getMasterChangeLog(ProjectConfig projectConfig, DBConfig dbConfig) {
        String masterContent = null;
        try {
            masterContent = Files.readString(Path.of(
                    projectConfig.path(),
                    projectConfig.dwhPath(),
                    buildChangeLogPath(projectConfig.changelogPathTemplate(), dbConfig.dbType(), dbConfig.database()),
                    "master.yaml"
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SQLFile master = new SQLFile(
                Path.of(buildChangeLogPath(projectConfig.changelogPathTemplate(), dbConfig.dbType(), dbConfig.database()), "master.yaml").toString(),
                masterContent);
        return master;
    }
}
