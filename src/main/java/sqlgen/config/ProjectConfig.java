package sqlgen.config;

import java.util.List;

public record ProjectConfig(
    String path,
    String dwhPath,
    String modelPath,
    String name,
    String tableMigrationPathTemplate,
    String changelogPathTemplate,
    String databasePathTemplate,
    MigrationNameConfig migrationNameConfig,
    String changelogNameTemplate,
    String changelogNoFormat,
    List<DBConfig> databases
) { }
