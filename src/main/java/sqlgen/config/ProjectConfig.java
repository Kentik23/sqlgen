package sqlgen.config;

public record ProjectConfig(
    String path,
    String name,
    DBConfig chConfig,
    DBConfig pgConfig,
    DBConfig gpConfig
) { }
