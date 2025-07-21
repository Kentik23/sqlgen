package sqlgen.config;

public record MigrationNameConfig(
    String fileNameTemplate,
    String migrationNoFormat
) {}
