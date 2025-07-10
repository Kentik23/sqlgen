package sqlgen;

import sqlgen.io.SQLWriter;

import java.io.File;
import java.util.List;

public class SqlGeneratorApplication {

    private static final String APPLICATION_PATH = "";
    private static final String CONFIG_PATH = APPLICATION_PATH + "/config/app.config";
    
    public static void run() {
        AppConfig appConfig = new AppConfig();
        DWHConfig dwhConfig = appConfig.getDWHConfig();
        ParserConfig parserConfig = appConfig.getParserConfig();
        GeneratorConfig generatorConfig = appConfig.getGeneratorConfig();


        SQLGenerator sqlGenerator = SQLGenerator.create(generatorConfig);
        List<File> sqlFiles = sqlGenerator.generate();
        SQLWriter sqlWriter = SQLWriter.create(sqlFiles);
        sqlWriter.saveAs(appConfig.getDefault().getSaveFolder());
    }
}
