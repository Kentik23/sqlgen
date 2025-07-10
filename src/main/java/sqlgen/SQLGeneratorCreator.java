package sqlgen;

import sqlgen.entity.SQLGenerator;

public class SQLGeneratorCreator {
    public static SQLGenerator create(AppConfig appConfig) {
        String typeGeneration = appConfig.getCurrentTypeGeneration();
        return switch (typeGeneration) {
            case "ClickHouse" -> new ClickHouseGenerator(appConfig);
            case "GreenPlum" -> new GreenPlumGenerator(appConfig);
            case "PostgreSQL" -> new PostgreSQLGenerator(appConfig);
            default -> new EmptyGenerator(appConfig);
        };
    }
}
