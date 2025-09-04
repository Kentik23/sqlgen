package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.generators.greenplum.model.GPTable;
import sqlgen.parcers.ChangeLogParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GISMUBI_28457 {
    private static void gp() {
        try {
            AppConfig appConfig = new AppConfig();
            SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig().gpConfig(), appConfig.getTaskConfig());

            List<GPTable> tables = new ArrayList<>();
            tables.add(new GPTable(
                    "dm_esfl",
                    "dim_person_processing",
                    null,
                    null,
                    14
            ));

            tables.add(new GPTable(
                    "dm_esfl",
                    "dim_person_hist",
                    null,
                    null,
                    18
            ));

            tables.add(new GPTable(
                    "dm_common",
                    "dim_person_link",
                    null,
                    null,
                    3
            ));

            tables.add(new GPTable(
                    "dm_common",
                    "dim_person_esfl_link",
                    null,
                    null,
                    2
            ));

            List<SQLFile> sqlFiles = new LinkedList<>();
            sqlFiles.add(sqlGenerator.addPartition(tables.get(0), "3688894719162586415", "p_fre"));
            sqlFiles.add(sqlGenerator.addPartition(tables.get(1), "3688894719162586415", "p_fre"));
            sqlFiles.add(sqlGenerator.addPartition(tables.get(2), "'fre'", "p_fre"));
            sqlFiles.add(sqlGenerator.addPartition(tables.get(3), "3688894719162586415", "p_fre"));

            sqlGenerator.addChangelogFiles(sqlFiles, "create-partitions", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().gpConfig()));

            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    public static void main(String[] args) {
        gp();
    }
}
