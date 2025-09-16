package sqlgenlib.config;

public record MigrationNameConfig(
    String fileNameTemplate,
    String migrationNoFormat
) {}
