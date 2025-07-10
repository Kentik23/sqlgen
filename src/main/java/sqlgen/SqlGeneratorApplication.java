package sqlgen;

import sqlgen.config.AppConfig;
import sqlgen.entity.SQLFile;
import sqlgen.entity.SQLGenerator;
import sqlgen.io.SQLWriter;

import java.util.List;

public class SqlGeneratorApplication {

    private static final String APPLICATION_PATH = "";
    private static final String CONFIG_PATH = APPLICATION_PATH + "/config/appConfig.yaml";
    
    public static void run() {
        AppConfig appConfig = new AppConfig(CONFIG_PATH);

        SQLGenerator sqlGenerator = SQLGeneratorCreator.create(appConfig);
        List<SQLFile> sqlFiles = sqlGenerator.generate();
        SQLWriter sqlWriter = new SQLWriter(sqlFiles);
        sqlWriter.saveAs(appConfig.getSaveFolder());
    }
}
