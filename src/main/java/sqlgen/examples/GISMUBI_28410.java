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

public class GISMUBI_28410 {
    private static void pg() {
        try {
            AppConfig appConfig = new AppConfig();

            List<GPTable> tables = new ArrayList<>();

            tables.add(new GPTable(
                    null,
                    "dm_checklist_actual",
                    null,
                    null,
                    2
            ));

            List<GPColumn> columns = new ArrayList<>();

            columns.add(new GPColumn(
                    "department_desc",
                    "text",
                    null,
                    "Орган, принявший решение",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().pgConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_portal", tables)), columns);

            List<String> neededTables = List.of(
                    "dm_checklist_actual_copy"
            );

            List<PDMTable> pdmTables = new PDMParser().fillTables(
                    Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().modelPath(), "/bi/model/bi_portal.pdm").toString(),
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

            sqlFiles.addAll(sqlGenerator.createTables(List.of(new GPSchema("dm_portal", gpTables))));

            sqlGenerator.addChangelogFiles(sqlFiles, "create-table", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().pgConfig()));

            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    private static void gp() {
        try {
            List<String> neededTables = List.of(
                    "dm_checklist_actual_portal"
            );

            AppConfig appConfig = new AppConfig();

            List<PDMTable> pdmTables = new PDMParser().fillTables(
                    Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().modelPath(), "/bi/model/bi_sgn.pdm").toString(),
                    neededTables
            );

            TableNormalizer normalizer = new TableNormalizer(appConfig.getNormalizerConfig().gpDomains());
            List<GPTable> gpTables = new ArrayList<>();

            for (PDMTable pdmTable : pdmTables) {
                //  normalizer.addSystemFields(pdmTable);
                normalizer.normalize(pdmTable, true);
                normalizer.sort(pdmTable.getColumns());
                GPTable gp = TableConverter.toGP(pdmTable);
                GPTable stg = new GPTable(gp);
                stg.setColumns(gp.getColumns());
                stg.setCode(gp.getCode().substring(0,gp.getCode().length() - 4) + "stg");
                gpTables.add(gp);
                gpTables.add(stg);
            }


            SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig().gpConfig(), appConfig.getTaskConfig());

            List<SQLFile> sqlFiles = sqlGenerator.createTables(List.of(new GPSchema("dm_sgn", gpTables)));
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
