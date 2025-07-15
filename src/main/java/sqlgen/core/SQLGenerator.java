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

    private SQLFile wrapSql(String relativePath, String content) {
        String relativePath = ;
        StringBuilder content = new StringBuilder();
    }

    private SQLFile getChangelogFiles(List<SQLFile> sqlFiles) {

    }

    private SQLFile generateSQLMigrationFile(Schema schema, Table table) {
        String relativePath = schema.getCode() + '/' + table.getCode() + '/' + generateFileName();
        StringBuilder content = new StringBuilder();



        return new SQLFile(relativePath, content);
    }

    private String generateFileName() {
        g
    }


    private

    public void setConfig() {

    }

    public List<GPSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<GPSchema> schemas) {
        this.schemas = schemas;
    }
}
