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
import sqlgen.parcers.ChangeLogParser;

import java.util.ArrayList;
import java.util.List;

public class GISMUBI_28290 {
    public static void gp() {
        try {
            AppConfig appConfig = new AppConfig();

            List<Table> tables = new ArrayList<>();

            tables.add(new GPTable(
                    null,
                    "dm_labm_patent",
                    null,
                    null,
                    6
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new GPColumn(
                    "person_phone",
                    "varchar(15)",
                    null,
                    "Телефон заявителя",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().gpConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_click", tables)), columns);

            sqlGenerator.addChangelogFiles(sqlFiles, "add-column", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().gpConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
    public static void click() {
        try {
            AppConfig appConfig = new AppConfig();

            List<Table> tables = new ArrayList<>();

            tables.add(new CHTable(
                    null,
                    "dm_labm_patent",
                    null,
                    null,
                    11
            ));

            tables.add(new CHTable(
                    null,
                    "dm_labm_patent_checklist",
                    null,
                    null,
                    4
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new CHColumn(
                    "person_phone",
                    "String",
                    null,
                    "Телефон заявителя",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().chConfig(),
                    appConfig.getTaskConfig()
            );
            sqlFiles = sqlGenerator.addColumns(List.of(new CHSchema("dm_public", tables)), columns);

            sqlGenerator.addChangelogFiles(sqlFiles, "add-column", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().chConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        gp();
        click();
    }
}
