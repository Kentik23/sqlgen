package sqlgen.kristinaHelper;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.generators.clickhouse.model.CHSchema;
import sqlgen.generators.clickhouse.model.CHTable;
import sqlgen.parcers.ChangeLogParser;
import sqlgen.parcers.pdmparcer.PDMParser;
import sqlgen.parcers.pdmparcer.model.PDMTable;
import sqlgen.utils.TableConverter;
import sqlgen.utils.TableNormalizer;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            List<PDMTable> pdmTables = new PDMParser().fillTables(appConfig.getProjectConfig(), appConfig.getTaskConfig().tablesList());
            TableNormalizer normalizer = new TableNormalizer(appConfig);
            for (PDMTable pdmTable : pdmTables) {
                normalizer.normalize(pdmTable);
            }
            List<CHTable> tables = pdmTables.stream().map(TableConverter::toCH).toList();


            List<CHTable> stgTables = new LinkedList<>();

            for (CHTable chTable : tables) {
                chTable.setWithDistributed(appConfig.getTaskConfig().generateDistributedTables());
                if (appConfig.getTaskConfig().generateSTGTables()) {
                    stgTables.add(
                            new CHTable(
                                    null,
                                    chTable.getCode() + "_stg",
                                    chTable.getComment(),
                                    chTable.getColumns(),
                                    chTable.getLastMigrationNo(),
                                    chTable.isWithDistributed()
                            )
                    );
                }
            }

            List<CHTable> allTables = Stream.concat(tables.stream(), stgTables.stream()).toList();

            List<SQLFile> sqlFiles;
            SQLGenerator sqlGenerator = new SQLGenerator(
                    appConfig.getProjectConfig().chConfig(),
                    appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.createTables(List.of(new CHSchema(appConfig.getTaskConfig().schema(), allTables)));

            if (appConfig.getTaskConfig().generateChangelogs())
                sqlGenerator.addChangelogFiles(sqlFiles, "create-table", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().chConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
