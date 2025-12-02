package sqlgenlib.examples;

import sqlgenlib.AppConfig;
import sqlgenlib.config.DBConfig;
import sqlgenlib.core.SQLGenerator;
import sqlgenlib.core.io.SQLFile;
import sqlgenlib.core.io.SQLWriter;
import sqlgenlib.parcers.ChangelogParser;
import sqlgenlib.parcers.DWHProjectParser;

import java.nio.file.Path;
import java.util.List;

public class ChangelogGenerator {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            for (DBConfig dbConfig : appConfig.getProjectConfig().databases()) {
                List<SQLFile> sqlFiles = DWHProjectParser.findTaskFiles(appConfig.getProjectConfig(), dbConfig, appConfig.getTaskConfig());
                if (!sqlFiles.isEmpty()) {
                    SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig(), dbConfig, appConfig.getTaskConfig());
                    sqlGenerator.addChangelogFiles(sqlFiles, "alter-table", ChangelogParser.getMasterChangeLog(appConfig.getProjectConfig(), dbConfig));
                    SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
