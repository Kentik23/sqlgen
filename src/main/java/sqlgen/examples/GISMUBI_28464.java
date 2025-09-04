package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.generators.greenplum.model.GPColumn;
import sqlgen.generators.greenplum.model.GPSchema;
import sqlgen.generators.greenplum.model.GPTable;
import sqlgen.parcers.ChangeLogParser;
import sqlgen.parcers.pdmparcer.PDMParser;
import sqlgen.parcers.pdmparcer.model.PDMTable;
import sqlgen.utils.TableConverter;
import sqlgen.utils.TableNormalizer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GISMUBI_28464 {
    private static void pg() {
        try {
            AppConfig appConfig = new AppConfig();

            List<GPTable> tables = new ArrayList<>();
            tables.add(new GPTable(
                    null,
                    "dm_nsud_fro_osk_fosk",
                    null,
                    null,
                    6
            ));

            List<GPColumn> columns = new ArrayList<>();
            columns.add(new GPColumn(
                    "system_number_id",
                    "bigint",
                    "-1",
                    "Системный номер",
                    true
            ));
            columns.add(new GPColumn(
                    "region_code_id",
                    "bigint",
                    "-1",
                    "Код региона",
                    true
            ));
            columns.add(new GPColumn(
                    "record_id",
                    "bigint",
                    "-1",
                    "Идентификатор записи",
                    true
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().pgConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.dropColumns(List.of(new GPSchema("dm_ern_nsud", tables)), columns);

            sqlGenerator.addChangelogFiles(sqlFiles, "create-table", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().pgConfig()));

            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    private static void gp() {
        try {
            AppConfig appConfig = new AppConfig();
            SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig().gpConfig(), appConfig.getTaskConfig());

            List<GPTable> tables2 = new ArrayList<>();
            tables2.add(new GPTable(
                    null,
                    "dm_nsud_fro_osk_fosk",
                    null,
                    null,
                    8
            ));

            List<GPColumn> columns2 = new ArrayList<>();
            columns2.add(new GPColumn(
                    "system_number_id",
                    "bigint",
                    "-1",
                    "Системный номер",
                    true
            ));
            columns2.add(new GPColumn(
                    "region_code_id",
                    "bigint",
                    "-1",
                    "Код региона",
                    true
            ));
            columns2.add(new GPColumn(
                    "record_id",
                    "bigint",
                    "-1",
                    "Идентификатор записи",
                    true
            ));

            List<SQLFile> sqlFiles = sqlGenerator.dropColumns(List.of(new GPSchema("dm_ern_nsud", tables2)), columns2);

            sqlGenerator.addChangelogFiles(sqlFiles, "create-tables", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().gpConfig()));

            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    public static void main(String[] args) {
        pg();
        gp();
    }
}
