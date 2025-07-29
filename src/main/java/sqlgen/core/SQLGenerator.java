package sqlgen.core;

import sqlgen.config.GeneratorConfig;
import sqlgen.config.DBConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.io.SQLFile;
import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.LinkedList;
import java.util.List;

public class SQLGenerator {
    private GeneratorConfig generatorConfig;
    private DBConfig dbConfig;
    private TaskConfig taskConfig;

    public SQLGenerator(GeneratorConfig generatorConfig, DBConfig dbConfig, TaskConfig taskConfig) {
        this.generatorConfig = generatorConfig;
        this.dbConfig = dbConfig;
        this.taskConfig = taskConfig;
    }

//    public List<SQLFile> createTables(List<GPSchema> schemas) {
//        List<SQLFile> sqlFiles = new LinkedList<>();
//
//        for (Schema schema : schemas) {
//            for (Table table : schema.getTables()) {
//                sqlFiles.add(
//                    new SQLFile(
//                        schema.getCode() + '/' + table.getCode() + '/' + generateFileName(),
//                        wrapSql(table.getCreateScript())
//                    )
//                );
//            }
//        }
//
//        return sqlFiles;
//    }

    public List<SQLFile> addColumns(List<Schema> schemas, List<Column> columns) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema schema : schemas) {
            for (Table table : schema.getTables()) {
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

    private String buildFilePath(String template, String schema, String table) {
        return template
                .replace("{schemePath}", dbConfig.schemePath())
                .replace("{scheme}", schema)
                .replace("{table}", table);
    }


    private String wrapSql(String filename, String sqlScript) {
        return "--liquibase formatted sql\n" +
                "--changeset " + taskConfig.username() + ":" + filename + " runInTransaction:true\n\n" +
                sqlScript;
    }

    private String generateFileName(String template, String migrationNoFormat, int prevMigrationNumber, String actionName) {
        String migrationNo = String.format(migrationNoFormat, prevMigrationNumber + 1);

        return template
                .replace("{taskNo}", taskConfig.taskNo())
                .replace("{migrationNo}", migrationNo)
                .replace("{actionName}", actionName);
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
        .append("  - logicalFilePath: ").append(logicalFilePath).append("\n");
        
        for (SQLFile sqlFile : sqlFiles) {
            content.append("  - include:\n")
            .append("      file: ").append(sqlFile.getRelativePath()).append("\n");
        }
        
        SQLFile changeLog = new SQLFile(logicalFilePath, content.toString());
        
        sqlFiles.add(newMaster);
        sqlFiles.add(changeLog);
    }
}
