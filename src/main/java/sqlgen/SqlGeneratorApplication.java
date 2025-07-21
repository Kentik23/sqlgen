package sqlgen;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.model.Column;
import sqlgen.core.model.Table;
import sqlgen.generators.greenplum.model.GPColumn;
import sqlgen.generators.greenplum.model.GPSchema;
import sqlgen.generators.greenplum.model.GPTable;

import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorApplication {
    public static void run() {
        try {
            AppConfig appConfig = new AppConfig();
            
            List<Table> tables = new ArrayList<>();

            tables.add(new GPTable(
                    null,
                    "test_table",
                    null,
                    null,
                    0
            ));

            List<Column> columns = new ArrayList<>();

            columns.add(new GPColumn(
                    "queue_number",
                    "dIdint",
                    null,
                    "",
                    false
            ));

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                null,
                appConfig.getProjectConfig(),
                appConfig.getTaskConfig()
            );
            
            

            sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("cpig_stg", tables)), columns);

            SQLFile master = new SQLFile(
                "src/main/ch/databases/default/_changelogs/master.yaml", 
                "databaseChangeLog:\n" + 
                "  - logicalFilePath: src/main/pg/_changelogs/master.yaml\n" +
                "  - include:\n" +
                "    file: tasks/0001-init-DITBIIG-6510.yaml\n" + 
                "    relativeToChangelogFile: true");

            sqlGenerator.addChangelogFiles(sqlFiles, "add-column", master);

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, "/home/alexey/projects/dwh");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
