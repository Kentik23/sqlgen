package sqlgen.config;

public record ProjectConfig(
    String path,
    String dwhPath,
    String modelPath,
    String name,
    DBConfig chConfig,
    DBConfig pgConfig,
    DBConfig gpConfig
) { }
