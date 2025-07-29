package sqlgen.config;

public record DBConfig(
        String tableMigrationPathTemplate,
        String schemePath,
        MigrationNameConfig migrationNameConfig,
        String changelogPath,
        String changelogNameTemplate,
        String changelogNoFormat
) {}
