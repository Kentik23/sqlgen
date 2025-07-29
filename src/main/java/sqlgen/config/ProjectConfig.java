package sqlgen.config;

public record ProjectConfig(
    String name,
    DBConfig chConfig,
    DBConfig pgConfig,
    DBConfig gpConfig
) { }
