package sqlgenlib.core;

import sqlgenlib.config.DBConfig;
import sqlgenlib.config.ProjectConfig;
import sqlgenlib.config.TaskConfig;
import sqlgenlib.core.io.SQLFile;
import sqlgenlib.core.model.Column;
import sqlgenlib.core.model.Schema;
import sqlgenlib.core.model.Table;
import sqlgenlib.utils.TemplateBuilder;

import java.io.IOException;
import java.util.LinkedList;
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

        String changelogName = TemplateBuilder.from(projectConfig.migrationNameConfig().fileNameTemplate())
                .with("migrationNo", String.format(projectConfig.migrationNameConfig().migrationNoFormat(), occ + 1))
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
