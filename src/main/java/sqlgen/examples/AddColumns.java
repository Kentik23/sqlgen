package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.model.Column;
import sqlgen.core.model.Table;
import sqlgen.generators.clickhouse.model.CHColumn;
import sqlgen.generators.clickhouse.model.CHSchema;
import sqlgen.generators.clickhouse.model.CHTable;
import sqlgen.parcers.ChangeLogParser;

import java.util.ArrayList;
import java.util.List;

public class AddColumns {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();
            
            List<Table> tables = new ArrayList<>();

            tables.add(new CHTable(
                    null,
                    "dim_age_group_hist",
                    null,
                    null,
                    2
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new CHColumn(
                    "rosstat2_age_group_name",
                    "String",
                    null,
                    "",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                null,
                appConfig.getProjectConfig().chConfig(),
                appConfig.getTaskConfig()
            );
            
            sqlFiles = sqlGenerator.addColumns(List.of(new CHSchema("dm_nsi", tables)), columns);

            sqlGenerator.addChangelogFiles(sqlFiles, "add-column", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().chConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
