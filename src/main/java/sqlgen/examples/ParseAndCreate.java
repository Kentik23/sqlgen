package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.config.DBConfig;
import sqlgen.generators.greenplum.model.GPTable;
import sqlgen.parcers.DWHProjectParser;

public class ParseAndCreate {

    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();
            DBConfig gpConfig = appConfig.getProjectConfig().gpConfig();
            GPTable table = DWHProjectParser.parseTable("fct_appeal", gpConfig, appConfig.getProjectConfig().path());

            System.out.println(table.getCreateScript());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
