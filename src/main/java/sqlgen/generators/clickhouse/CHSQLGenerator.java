package sqlgen.generators.clickhouse;

import sqlgen.config.DBConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.LinkedList;
import java.util.List;

public class CHSQLGenerator extends SQLGenerator {
    public CHSQLGenerator(DBConfig dbConfig, TaskConfig taskConfig) {
        super(dbConfig, taskConfig);
    }

    public List<SQLFile> createDictionaries(List<Schema> schemas) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (Schema schema : schemas) {
            for (Table table : schema.getTables()) {
                String filename = generateFileName(
                        getDbConfig().migrationNameConfig().fileNameTemplate(),
                        getDbConfig().migrationNameConfig().migrationNoFormat(),
                        table.getLastMigrationNo(),
                        "create-dictionaries"
                );
                sqlFiles.add(
                        new SQLFile(
                                this.buildFilePath(getDbConfig().tableMigrationPathTemplate(), schema.getCode(), table.getCode())
                                        + filename,
                                this.wrapSql(filename, table.getCreateScript())
                        )
                );
            }
        }

        return sqlFiles;
    }
}
