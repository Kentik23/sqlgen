package sqlgenlib.generators.clickhouse;

import sqlgenlib.config.DBConfig;
import sqlgenlib.config.ProjectConfig;
import sqlgenlib.config.TaskConfig;
import sqlgenlib.core.SQLGenerator;
import sqlgenlib.core.io.SQLFile;
import sqlgenlib.generators.clickhouse.model.CHSchema;
import sqlgenlib.generators.clickhouse.model.CHTable;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CHSQLGenerator extends SQLGenerator {
    public CHSQLGenerator(ProjectConfig projectConfig, DBConfig dbConfig, TaskConfig taskConfig) throws IOException {
        super(projectConfig, dbConfig, taskConfig);
    }

    public List<SQLFile> createDictionaries(List<CHSchema> schemas) {
        List<SQLFile> sqlFiles = new LinkedList<>();

        for (CHSchema schema : schemas) {
            for (CHTable table : schema.getTables()) {
                String filename = generateFileName(
                        getProjectConfig().migrationNameConfig().fileNameTemplate(),
                        getProjectConfig().migrationNameConfig().migrationNoFormat(),
                        table.getLastMigrationNo(),
                        "create-dictionary"
                );
                sqlFiles.add(
                        new SQLFile(
                                this.buildFilePath(getProjectConfig().tableMigrationPathTemplate(), getDbConfig().dbType(), getDbConfig().database(), schema.getCode(), table.getCode())
                                        + filename,
                                this.wrapSql(filename, table.getCreateScript())
                        )
                );
            }
        }

        return sqlFiles;
    }
}
