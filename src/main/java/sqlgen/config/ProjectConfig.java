package sqlgen.config;

public record ProjectConfig(
        String tableMigrationPathTemplate,
        String schemePath,
        MigrationNameConfig migrationNameConfig,
        String changelogPath,
        String changelogNameTemplate,
        String changelogNoFormat
) {}
