package sqlgen;

import sqlgen.config.AppConfig;

public class SqlGeneratorApplication {
    public static void run() {
        try {
            AppConfig appConfig = new AppConfig();
            System.out.println(appConfig.getProperty("test"));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
