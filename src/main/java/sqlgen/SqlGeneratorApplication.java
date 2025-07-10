package sqlgen;

import sqlgen.entity.SQLFile;
import sqlgen.entity.SQLGenerator;
import sqlgen.io.SQLWriter;

import java.util.List;

public class SqlGeneratorApplication {

    private static final String APPLICATION_PATH = "";
    private static final String CONFIG_PATH = APPLICATION_PATH + "/config/app.config";
    
    public static void run() {
        AppConfig appConfig = new AppConfig(CONFIG_PATH);
        DWHConfig dwhConfig = appConfig.getDWHConfig();
        ParserConfig parserConfig = appConfig.getParserConfig();
        GeneratorConfig generatorConfig = appConfig.getGeneratorConfig();


        SQLGenerator sqlGenerator = SQLGeneratorCreator.create(generatorConfig);
        List<SQLFile> sqlFiles = sqlGenerator.generate();
        SQLWriter sqlWriter = new SQLWriter(sqlFiles);
        sqlWriter.saveAs(appConfig.getDefault().getSaveFolder());
    }
}
