package sqlgen.core;

import sqlgen.core.io.SQLFile;
import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;
import sqlgen.generators.greenplum.model.GPSchema;

import java.util.LinkedList;
import java.util.List;

public class SQLGenerator {
    private GeneratorConfig config;

    public SQLGenerator(GeneratorConfig config) {
        this.config = config;
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
                sqlFiles.add(
                        new SQLFile(
                                config.schemePath() + '/' + schema.getCode() + "/tables/" + table.getCode() + '/' + generateFileName(),
                                wrapSql(table.getAddColumnsScript(columns))
                        )
                );
            }
        }

        return sqlFiles;
    }

    private String wrapSql(String sqlScript) {
        StringBuilder content = new StringBuilder();
        content.append("--liquibase formatted sql\n")
                .append("--changeset ").append(config.username()).append(":").append(generateFileName()).append(" runInTransaction:true\n\n");
        content.append(sqlScript);
        return content.toString();
    }

    private String generateFileName() {
        return "1-0-0-CD-" + config.taskNo() + "-add_column.sql";
    }
    
    public void addChangelogFiles(List<SQLFile> sqlFiles) {
        String logicalFilePath = config.changeLogPath() + "/tasks/" + config.taskNo() + ".yaml";

        StringBuilder content = new StringBuilder();
        content.append("databaseChangeLog:\n")
                .append("  - logicalFilePath: ").append(logicalFilePath).append("\n");

        for (SQLFile sqlFile : sqlFiles) {
            content.append("  - include:\n")
                    .append("      file: ").append(sqlFile.getRelativePath()).append("\n");
        }

        SQLFile changeLog = new SQLFile(logicalFilePath, content.toString());

        sqlFiles.add(changeLog);
    }
}
