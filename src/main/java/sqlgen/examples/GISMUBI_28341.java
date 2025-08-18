package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
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

public class GISMUBI_28341 {
    public static void main(String[] args) {
        try {
            List<String> neededTables = List.of(
                    "dm_report9",
                    "dim_country",
                    "dim_age_group",
                    "dim_region",
                    "dim_sex",
                    "dim_federal_district",
                    "dim_arrival_goal"
            );

            AppConfig appConfig = new AppConfig();

            List<PDMTable> pdmTables = new PDMParser().fillTables(appConfig.getProjectConfig(), neededTables);

            TableNormalizer normalizer = new TableNormalizer(appConfig.getNormalizerConfig().gpDomains());
            List<GPTable> gpTables = new ArrayList<>();
            for (PDMTable pdmTable : pdmTables) {
                normalizer.normalize(pdmTable);
                GPTable gp = TableConverter.toGP(pdmTable);
                gpTables.add(gp);
            }


            SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig().gpConfig(), appConfig.getTaskConfig());

            List<SQLFile> sqlFiles = sqlGenerator.createTables(List.of(new GPSchema("dm_asao", gpTables)));
            sqlGenerator.addChangelogFiles(sqlFiles, "create-tables", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().gpConfig()));

            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }
}
