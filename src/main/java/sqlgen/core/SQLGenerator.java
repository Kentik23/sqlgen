package sqlgen.core;

import sqlgen.core.io.SQLFile;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;
import sqlgen.generators.greenplum.model.GPSchema;

import java.util.LinkedList;
import java.util.List;

public abstract class SQLGenerator {
    private GeneratorConfig config;

    public List<SQLFile> createTable(List<GPSchema> schemas) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema schema : schemas) {
            for (Table table : schema.getTables()) {
                sqlFiles.add(wrapSql(
                        schema.getCode() + '/' + table.getCode() + '/' + generateFileName(),
                        table.getCreateScript()
                ));
            }
        }

        sqlFiles.add(getChangelogFiles(sqlFiles));

        return sqlFiles;
    }

    private SQLFile wrapSql(String relativePath, String sqlScript) {
        StringBuilder content = new StringBuilder();
        content.append(
            "--liquibase formatted sql\n"
                + "--changeset " + config.getUsername() + ":" + generateFileName() + " runInTransaction:true\n"
        );


    }

    private String generateFileName() {
        return ":1-0-0-CD-" + config.getTaskNo() + "-init.sql";
    }
    
    private SQLFile generateSQLMigrationFile(Schema schema, Table table) {
        String relativePath = schema.getCode() + '/' + table.getCode() + '/' + generateFileName();
        StringBuilder content = new StringBuilder();



        return new SQLFile(relativePath, content);
    }
    
    private SQLFile getChangelogFiles(List<SQLFile> sqlFiles) {

    }
}
