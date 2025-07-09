package sqlgen;

public class SqlGeneratorApplication {

    private static final String APPLICATION_PATH = "";
    private static final String CONFIG_PATH = APPLICATION_PATH + "/config/app.config";
    
    public static void run() {
        AppConfig appConfig = new AppConfig();
        ParserConfig parserConfig = appConfig.getParserConfig();
        GeneratorConfig generatorConfig = appConfig.getGeneratorConfig();


        SqlGenerator sqlGenerator = SqlGenerator.createFromConfig(appConfig);
        sqlGenerator.saveInFolder();
    }
}
