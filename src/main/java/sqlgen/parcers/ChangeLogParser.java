package sqlgen.parcers;

import sqlgen.config.AppConfig;
import sqlgen.config.DBConfig;
import sqlgen.core.io.SQLFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChangeLogParser {
    public static SQLFile getMasterChangeLog(AppConfig appConfig, DBConfig dbConfig) {
        String masterContent = null;
        try {
            masterContent = Files.readString(Path.of(
                    appConfig.getProjectConfig().path(),appConfig.getProjectConfig().dwhPath(), dbConfig.changelogPath(), "master.yaml"
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SQLFile master = new SQLFile(
                Path.of(dbConfig.changelogPath(), "master.yaml").toString(),
                masterContent);
        return master;
    }
}
