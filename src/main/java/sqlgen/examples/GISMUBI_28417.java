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

public class GISMUBI_28417 {
    private static void pg() {
        try {
            AppConfig appConfig = new AppConfig();

            List<GPTable> tables = new ArrayList<>();
            tables.add(new GPTable(
                    null,
                    "dm_nsud_fro_osk_fosk",
                    null,
                    null,
                    5
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

            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_ern_nsud", tables)), columns);

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

            List<GPTable> tables = new ArrayList<>();
            tables.add(new GPTable(
                    null,
                    "fct_passport_change_info",
                    null,
                    null,
                    1
            ));

            List<GPColumn> columns = new ArrayList<>();
            columns.add(new GPColumn(
                    "person_id",
                    "bigint",
                    "-1",
                    "ИДФЛ персоны",
                    true
            ));
            columns.add(new GPColumn(
                    "fio_desc",
                    "text",
                    null,
                    "ФИО",
                    false
            ));
            columns.add(new GPColumn(
                    "fi_desc",
                    "text",
                    null,
                    "ФИ",
                    false
            ));
            columns.add(new GPColumn(
                    "person_hash_code",
                    "varchar(128)",
                    "'N/D'",
                    "Хеш ФЛ по ФИО и ДР",
                    true
            ));
            List<SQLFile> sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_rfp", tables)), columns);


            List<String> neededTables = List.of(
                    "dm_conviction_link_rfp"
            );

            List<PDMTable> pdmTables = new PDMParser().fillTables(
                    Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().modelPath(), "/bi/model/bi_nsud_rg.pdm").toString(),
                    neededTables
            );

            TableNormalizer normalizer = new TableNormalizer(appConfig.getNormalizerConfig().gpDomains());
            List<GPTable> gpTables = new ArrayList<>();

            for (PDMTable pdmTable : pdmTables) {
                normalizer.normalize(pdmTable, true);
                normalizer.sort(pdmTable.getColumns());
                GPTable gp = TableConverter.toGP(pdmTable);
                gpTables.add(gp);
            }

            sqlFiles.addAll(sqlGenerator.createTables(List.of(new GPSchema("dm_ern_nsud", gpTables))));


            List<GPTable> tables2 = new ArrayList<>();
            tables2.add(new GPTable(
                    null,
                    "dm_nsud_fro_osk_fosk",
                    null,
                    null,
                    7
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
            sqlFiles.addAll(sqlGenerator.addColumns(List.of(new GPSchema("dm_ern_nsud", tables2)), columns2));


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
