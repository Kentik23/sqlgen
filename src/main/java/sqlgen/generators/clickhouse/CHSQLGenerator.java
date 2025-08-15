package sqlgen.generators.clickhouse;

import sqlgen.config.DBConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.generators.clickhouse.model.CHSchema;
import sqlgen.generators.clickhouse.model.CHTable;

import java.util.LinkedList;
import java.util.List;

public class CHSQLGenerator extends SQLGenerator {
    public CHSQLGenerator(DBConfig dbConfig, TaskConfig taskConfig) {
        super(dbConfig, taskConfig);
    }

    public List<SQLFile> createDictionaries(List<CHSchema> schemas) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (CHSchema schema : schemas) {
            for (CHTable table : schema.getTables()) {
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
