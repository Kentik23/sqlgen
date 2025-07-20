package sqlgen;

import sqlgen.config.AppConfig;
import sqlgen.config.MigrationNameConfig;
import sqlgen.config.ProjectConfig;
import sqlgen.config.TaskConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.io.SaveFileException;
import sqlgen.core.model.Column;
import sqlgen.core.model.Table;
import sqlgen.generators.greenplum.model.GPColumn;
import sqlgen.generators.greenplum.model.GPSchema;
import sqlgen.generators.greenplum.model.GPTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorApplication {
    public static void run() {
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
            new ProjectConfig(
                "{schemePath}/{scheme}/table/{table}/",
                "src/main/pg/databases/dwh/schemas", 
                new MigrationNameConfig(
                    "{migrationNo}-{actionName}-{taskNo}.sql",
                    "%03d-000-000"
                    ), 
                "src/main/pg/_changelogs", 
                "{migrationNo}-{actionName}-{taskNo}.yaml", 
                "%04d"),
            new TaskConfig(
                "aleandivanvov",
                "DITBIIG-6525")
            );
        
        

        sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("cpig_stg", tables)), columns);

        SQLFile master = new SQLFile(
            "src/main/pg/_changelogs/master.yaml", 
            "databaseChangeLog:\n" + 
            "  - logicalFilePath: src/main/pg/_changelogs/master.yaml\n" +
            "  - include:\n" +
            "    file: tasks/0001-init-DITBIIG-6510.yaml\n" + 
            "    relativeToChangelogFile: true\n");

        sqlGenerator.addChangelogFiles(sqlFiles, "add-column", master);

        SQLWriter sqlWriter = new SQLWriter();
        try {
            sqlWriter.saveAs(sqlFiles, "/home/alexey/projects/dwh");
        } catch (SaveFileException e) {
            System.out.println("Невозмоно сохранить файл");
            e.printStackTrace(System.err);
        }
        
        try {
            AppConfig appConfig = new AppConfig();
        } catch (IOException e) {
            System.err.println("d");
        }
    }
}
