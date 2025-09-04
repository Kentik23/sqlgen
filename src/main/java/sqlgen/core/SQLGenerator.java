package sqlgen.core;

import sqlgen.config.DBConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.io.SQLFile;
import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.LinkedList;
import java.util.List;

public class SQLGenerator {
    private DBConfig dbConfig;
    private TaskConfig taskConfig;

    public SQLGenerator(DBConfig dbConfig, TaskConfig taskConfig) {
        this.dbConfig = dbConfig;
        this.taskConfig = taskConfig;
    }

    protected String buildFilePath(String template, String schema, String table) {
        return template
                .replace("{schemePath}", dbConfig.schemePath())
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

        String changelogName = this.generateFileName(dbConfig.changelogNameTemplate(), dbConfig.changelogNoFormat(), occ, actionName);

        masterContent.append(
                "\n- include:\n" +
                        "    file: tasks/" + changelogName + "\n" +
                        "    relativeToChangelogFile: true"
        );
        SQLFile newMaster = new SQLFile(master.getRelativePath(), masterContent.toString());

        String logicalFilePath = dbConfig.changelogPath() + "/tasks/" + changelogName;


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

    public List<SQLFile> createTables(List<Schema<? extends Table<? extends Column>>> schemas) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema<? extends Table<? extends Column>> schema : schemas) {
            for (Table<? extends Column> table : schema.getTables()) {
                String filename = generateFileName(
                        dbConfig.migrationNameConfig().fileNameTemplate(),
                        dbConfig.migrationNameConfig().migrationNoFormat(),
                        table.getLastMigrationNo(),
                        "create-table"
                );
                sqlFiles.add(
                        new SQLFile(
                                this.buildFilePath(dbConfig.tableMigrationPathTemplate(), schema.getCode(), table.getCode())
                                        + filename,
                                this.wrapSql(filename, table.getCreateScript())
                        )
                );
            }
        }

        return sqlFiles;
    }

    public <C extends Column> List<SQLFile> addColumns(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema<? extends Table<C>> schema : schemas) {
            for (Table<C> table : schema.getTables()) {
                String filename = generateFileName(
                        dbConfig.migrationNameConfig().fileNameTemplate(),
                        dbConfig.migrationNameConfig().migrationNoFormat(),
                        table.getLastMigrationNo(),
                        "add-column"
                );
                sqlFiles.add(
                        new SQLFile(
                                this.buildFilePath(dbConfig.tableMigrationPathTemplate(), schema.getCode(), table.getCode())
                                        + filename,
                                this.wrapSql(filename, table.getAddColumnsScript(columns))
                        )
                );
            }
        }

        return sqlFiles;
    }

    public <C extends Column> List<SQLFile> dropColumns(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema<? extends Table<C>> schema : schemas) {
            for (Table<C> table : schema.getTables()) {
                String filename = generateFileName(
                        dbConfig.migrationNameConfig().fileNameTemplate(),
                        dbConfig.migrationNameConfig().migrationNoFormat(),
                        table.getLastMigrationNo(),
                        "drop_column"
                );
                sqlFiles.add(
                        new SQLFile(
                                this.buildFilePath(dbConfig.tableMigrationPathTemplate(), schema.getCode(), table.getCode())
                                        + filename,
                                this.wrapSql(filename, table.getDropColumnsScript(columns))
                        )
                );
            }
        }

        return sqlFiles;
    }
}
