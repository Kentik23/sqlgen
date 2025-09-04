package sqlgen.config;

import java.util.List;

public record ProjectConfig(
    String path,
    String dwhPath,
    String modelPath,
    String name,
    String tableMigrationPathTemplate,
    MigrationNameConfig migrationNameConfig,
    String changelogPathTemplate,
    String changelogNameTemplate,
    String changelogNoFormat,
    List<DBConfig> databases
) { }
