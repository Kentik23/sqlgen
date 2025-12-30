package sqlgenlib.core;

import sqlgenlib.config.DBConfig;
import sqlgenlib.config.ProjectConfig;
import sqlgenlib.config.TaskConfig;
import sqlgenlib.core.io.SQLFile;
import sqlgenlib.modules.clickhouse.model.CHColumn;
import sqlgenlib.modules.clickhouse.model.Dictionary;
import sqlgenlib.utils.TemplateBuilder;

import java.io.IOException;
import java.util.List;

public class SQLGenerator {
    private ProjectConfig projectConfig;
    private DBConfig dbConfig;
    private TaskConfig taskConfig;
    public SQLGenerator(ProjectConfig projectConfig, DBConfig dbConfig, TaskConfig taskConfig) throws IOException {
        this.projectConfig = projectConfig;
        this.dbConfig = dbConfig;
        this.taskConfig = taskConfig;
    }

    protected String buildFilePath(String template, String dbType, String database, String schema, String table) {
        return template
                .replace("{dbType}", dbType)
                .replace("{database}", database)
                .replace("{scheme}", schema)
                .replace("{table}", table);
    }

    protected String wrapSql(String filename, String sqlScript) {
        return "--liquibase formatted sql\n" +
                "--changeset " + taskConfig.username() + ":" + filename + " runInTransaction:true\n\n" +
                sqlScript.trim();
    }

    protected String generateFileName(String template, String migrationNoFormat, int prevMigrationNumber, String actionName) {
        String migrationNo = String.format(migrationNoFormat, prevMigrationNumber + 1);

        return template
                .replace("{taskNo}", taskConfig.taskNo())
                .replace("{migrationNo}", migrationNo)
                .replace("{actionName}", actionName);
    }

    public ProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public void setProjectConfig(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public DBConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void addChangelogFiles(List<SQLFile> sqlFiles, String actionName, SQLFile master) {
        StringBuilder masterContent = new StringBuilder(master.getContent());

        // Находим кол-во миграций))
        String s = master.getContent();
        String sub = "include";
        String temp = s.replace(sub, "");
        int occ = (s.length() - temp.length()) / sub.length();

        String changelogName = TemplateBuilder.from(projectConfig.changelogNameTemplate())
                .with("migrationNo", String.format(projectConfig.changelogNoFormat(), occ + 1))
                .with("actionName", "create-table")
                .with("taskNo", taskConfig.taskNo())
                .build();

        masterContent.append(
                "\n- include:\n" +
                        "    file: tasks/" + changelogName + "\n" +
                        "    relativeToChangelogFile: true"
        );
        SQLFile newMaster = new SQLFile(master.getRelativePath(), masterContent.toString());

        String logicalFilePath = buildFilePath(projectConfig.changelogPathTemplate(), dbConfig.dbType(), dbConfig.database(), "", "") + "/tasks/" + changelogName;


        StringBuilder content = new StringBuilder();
        content.append("databaseChangeLog:\n")
                .append("  - logicalFilePath: ").append(logicalFilePath);

        for (SQLFile sqlFile : sqlFiles) {
            content.append("\n  - include:\n")
                    .append("      file: ").append(sqlFile.getRelativePath());
        }

        SQLFile changeLog = new SQLFile(logicalFilePath, content.toString());

        sqlFiles.add(newMaster);
        sqlFiles.add(changeLog);
    }

    public SQLFile reinitDictionary (Dictionary dict) {
        StringBuilder sb = new StringBuilder();

        Dictionary oldDict = new Dictionary(dict);

        String schema = dict.getSchemaCode();
        String dictName = dict.getCode();
        List<CHColumn> columns = dict.getColumns();

        // Drop dictionary (текущий)
        sb.append("drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main sync;\n\n");

        // Create or replace dictionary (текущий)
        sb.append("create dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n(\n");

        for (int i = 0; i < columns.size(); i++) {
            CHColumn col = columns.get(i);
            sb.append("    ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());

            if (col.getComment() != null && !col.getComment().isBlank()) {
                sb.append(" comment '").append(col.getComment().replace("'", "''")).append("'");
            }

            if (i < columns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append(")\n");

        columns.stream().filter(CHColumn::isPrimary).findFirst()
                .ifPresent(chColumn -> sb.append("primary key ").append(chColumn.getCode()).append('\n'));

        sb.append("source(clickhouse(DB '")
                .append(dict.getSourceDatabase())
                .append("' table '")
                .append(dict.getSourceTable())
                .append("'))\n")
                .append("layout(hashed())\n")
                .append("lifetime(14400)");

        if (dict.getComment() != null && !dict.getComment().isBlank()) {
            sb.append("\ncomment '").append(dict.getComment().replace("'", "''")).append("'");
        }

        sb.append(";\n\n");

        // ---------- Rollback block (СТАРЫЙ словарь) ----------
        sb.append("--rollback drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main sync;\n");

        sb.append("--rollback create dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n")
                .append("--rollback (\n");

        List<CHColumn> oldColumns = oldDict.getColumns();
        for (int i = 0; i < oldColumns.size(); i++) {
            CHColumn col = oldColumns.get(i);
            sb.append("--rollback     ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());

            if (col.getComment() != null && !col.getComment().isBlank()) {
                sb.append(" comment '").append(col.getComment().replace("'", "''")).append("'");
            }

            if (i < oldColumns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append("--rollback )\n");

        oldColumns.stream().filter(CHColumn::isPrimary).findFirst()
                .ifPresent(chColumn -> sb.append("--rollback primary key ").append(chColumn.getCode()).append('\n'));

        sb.append("--rollback source(clickhouse(user '${CH_CICD_USER}' password '${CH_CICD_PASSWORD}' DB '")
                .append(oldDict.getSourceDatabase())
                .append("' table '")
                .append(oldDict.getSourceTable())
                .append("'))\n")
                .append("--rollback layout(hashed())\n")
                .append("--rollback lifetime(14400)");

        if (oldDict.getComment() != null && !oldDict.getComment().isBlank()) {
            sb.append("\n--rollback comment '").append(oldDict.getComment().replace("'", "''")).append("'");
        }

        sb.append(";\n");

        String filename = generateFileName(
                projectConfig.migrationNameConfig().fileNameTemplate(),
                projectConfig.migrationNameConfig().migrationNoFormat(),
                dict.getLastMigrationNo(),
                "reinit-dictionary"
        );

        return new SQLFile(
                this.buildFilePath(projectConfig.tableMigrationPathTemplate(), dbConfig.dbType(), dbConfig.database(), schema, dictName)
                        + filename,
                this.wrapSql(filename, sb.toString())
        );
    }

//    public List<SQLFile> createTables(List<Schema<? extends Table<? extends Column>>> schemas) {
//        List<SQLFile> sqlFiles = new LinkedList<>();
//
//        for (Schema<? extends Table<? extends Column>> schema : schemas) {
//            for (Table<? extends Column> table : schema.getTables()) {
//                String filename = generateFileName(
//                        projectConfig.migrationNameConfig().fileNameTemplate(),
//                        projectConfig.migrationNameConfig().migrationNoFormat(),
//                        table.getLastMigrationNo(),
//                        "create-table"
//                );
//                sqlFiles.add(
//                        new SQLFile(
//                                this.buildFilePath(projectConfig.tableMigrationPathTemplate(), dbConfig.dbType(), dbConfig.database(), schema.getCode(), table.getCode())
//                                        + filename,
//                                this.wrapSql(filename, table.getCreateScript())
//                        )
//                );
//            }
//        }
//
//        return sqlFiles;
//    }

//    public <C extends Column> List<SQLFile> addColumns(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
//        List<SQLFile> sqlFiles = new LinkedList<>();
//
//        for (Schema<? extends Table<C>> schema : schemas) {
//            for (Table<C> table : schema.getTables()) {
//                String filename = generateFileName(
//                        projectConfig.migrationNameConfig().fileNameTemplate(),
//                        projectConfig.migrationNameConfig().migrationNoFormat(),
//                        table.getLastMigrationNo(),
//                        "add-column"
//                );
//                sqlFiles.add(
//                        new SQLFile(
//                                this.buildFilePath(projectConfig.tableMigrationPathTemplate(), dbConfig.dbType(), dbConfig.database(), schema.getCode(), table.getCode())
//                                        + filename,
//                                this.wrapSql(filename, table.getAddColumnsScript(columns))
//                        )
//                );
//            }
//        }
//
//        return sqlFiles;
//    }
//
//    public SQLFile addPartition(Table<? extends Column> table, String value, String partitionName) {
//        String filename = generateFileName(
//                projectConfig.migrationNameConfig().fileNameTemplate(),
//                projectConfig.migrationNameConfig().migrationNoFormat(),
//                table.getLastMigrationNo(),
//                "add_partition"
//        );
//        return new SQLFile(
//                this.buildFilePath(projectConfig.tableMigrationPathTemplate(), dbConfig.dbType(), dbConfig.database(), table.getSchemaCode(), table.getCode())
//                        + filename,
//                this.wrapSql(filename, table.getCreatePartitionScript(partitionName, List.of(value)))
//        );
//    }
//
//    public <C extends Column> List<SQLFile> dropColumns(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
//        List<SQLFile> sqlFiles = new LinkedList<>();
//
//        for (Schema<? extends Table<C>> schema : schemas) {
//            for (Table<C> table : schema.getTables()) {
//                String filename = generateFileName(
//                        projectConfig.migrationNameConfig().fileNameTemplate(),
//                        projectConfig.migrationNameConfig().migrationNoFormat(),
//                        table.getLastMigrationNo(),
//                        "drop_column"
//                );
//                sqlFiles.add(
//                        new SQLFile(
//                                this.buildFilePath(projectConfig.tableMigrationPathTemplate(), dbConfig.dbType(), dbConfig.database(), schema.getCode(), table.getCode())
//                                        + filename,
//                                this.wrapSql(filename, table.getDropColumnsScript(columns))
//                        )
//                );
//            }
//        }
//
//        return sqlFiles;
//    }
}
