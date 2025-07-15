package sqlgen.generators.greenplum;

import sqlgen.core.io.SQLFile;
import sqlgen.core.SQLGenerator;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;
import sqlgen.generators.greenplum.model.GPSchema;

import java.util.LinkedList;
import java.util.List;

public class GPGenerator implements SQLGenerator {
    private List<GPSchema> schemas;
    private GPConfig config;

    @Override
    public List<SQLFile> createTable() {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema schema : schemas) {
            for (Table table : schema.getTables()) {
                sqlFiles.add(generateSQLFile(schema, table));
            }
        }

        sqlFiles.add(getChangelogFiles());

        return sqlFiles;
    }

    private SQLFile generateSQLFile(Schema schema, Table table) {
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
