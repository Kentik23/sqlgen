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

public class GISMUBI_28219 {
    public static void gp() {
        try {
            AppConfig appConfig = new AppConfig();

            List<Table> tables = new ArrayList<>();

            tables.add(new GPTable(
                    null,
                    "dm_uni_stay_person",
                    null,
                    null,
                    7
            ));

            tables.add(new GPTable(
                    null,
                    "dm_uni_stay_person_retrospective",
                    null,
                    null,
                    3
            ));

            tables.add(new GPTable(
                    null,
                    "dm_uni_stay_person_1126",
                    null,
                    null,
                    3
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new GPColumn(
                    "checklist_bool",
                    "boolean",
                    "false",
                    "Признак нахождения в КС",
                    true
            ));

            columns.add(new GPColumn(
                    "rkl_bool",
                    "boolean",
                    "false",
                    "Признак нахождения в РКЛ",
                    true
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().gpConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_mig", tables)), columns);

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
                    "dm_uni_stay_person",
                    null,
                    null,
                    2
            ));

            tables.add(new CHTable(
                    null,
                    "dm_uni_stay_person_retrospective",
                    null,
                    null,
                    2
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new CHColumn(
                    "checklist_bool",
                    "Bool",
                    "0",
                    "Признак нахождения в КС",
                    true
            ));

            columns.add(new CHColumn(
                    "rkl_bool",
                    "Bool",
                    "0",
                    "Признак нахождения в РКЛ",
                    true
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
