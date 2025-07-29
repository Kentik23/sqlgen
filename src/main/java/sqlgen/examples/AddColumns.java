package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.model.Column;
import sqlgen.core.model.Table;
import sqlgen.generators.clickhouse.model.CHColumn;
import sqlgen.generators.clickhouse.model.CHTable;
import sqlgen.generators.greenplum.model.GPSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AddColumns {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();
            
            List<Table> tables = new ArrayList<>();

            tables.add(new CHTable(
                    null,
                    "test_table",
                    null,
                    null,
                    0
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new CHColumn(
                    "queue_number",
                    "dIdint",
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
            
            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("cpig_stg", tables)), columns);

            String masterContent = null;
            try {
                masterContent = Files.readString(Path.of(
                        appConfig.getProjectConfig().path(), appConfig.getProjectConfig().chConfig().changelogPath(), "master.yaml"
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SQLFile master = new SQLFile(
                    Path.of(appConfig.getProjectConfig().chConfig().changelogPath(), "master.yaml").toString(),
                    masterContent);

            sqlGenerator.addChangelogFiles(sqlFiles, "add-column", master);

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
