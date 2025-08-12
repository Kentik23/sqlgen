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
import sqlgen.generators.greenplum.model.GPColumn;
import sqlgen.generators.greenplum.model.GPSchema;
import sqlgen.generators.greenplum.model.GPTable;
import sqlgen.generators.postgresql.PGColumn;
import sqlgen.generators.postgresql.PGSchema;
import sqlgen.generators.postgresql.PGTable;
import sqlgen.parcers.ChangeLogParser;

import java.util.ArrayList;
import java.util.List;

public class DITBIIG_7829 {
    public static void pg() {
        try {
            AppConfig appConfig = new AppConfig();

            List<Table> tables = new ArrayList<>();

            tables.add(new PGTable(
                    null,
                    "dim_ogdr_hist",
                    null,
                    null,
                    7
            ));

            tables.add(new PGTable(
                    null,
                    "dim_ogdr",
                    null,
                    null,
                    5
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new PGColumn(
                    "sign_update_dttm",
                    "dDateTime",
                    null,
                    "Дата обновления признака",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().pgConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.addColumns(List.of(new PGSchema("cpig_stg", tables)), columns);

            sqlGenerator.addChangelogFiles(sqlFiles, "alter-table", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().pgConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        pg();
    }
}
