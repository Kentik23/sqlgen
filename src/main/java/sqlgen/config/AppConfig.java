package sqlgen.config;

import sqlgen.SqlGeneratorApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class AppConfig extends Properties {
    private static final String APP_DIR = System.getProperty("user.dir");
    private static final String RELATIVE_CONFIG_PATH = "/config/app.properties";
    private static final List<String> DEFAULT_KEYS = List.of(
            "test",
            "clickhouse.config",
            "greenplum.config",
            "postgresql.config"
    );

    public AppConfig() throws IOException {
        parseConfig();
    }

    private void parseConfig() throws IOException {
        File configFile = new File(APP_DIR + RELATIVE_CONFIG_PATH);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                super.load(fis);
            }
        } else {
            createDefaultConfig(configFile);
        }
    }

    private void createDefaultConfig(File configFile) throws IOException {
        // default values
        this.setProperty("test", "default_test_value");
        this.setProperty("clickhouse.config", "config/clickhouse.yaml");
        this.setProperty("greenplum.config", "config/greenplum.yaml");
        this.setProperty("postgresql.config", "config/postgresql.yaml");

        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                System.err.println("Could not create config directory: " + parentDir.getAbsolutePath());
            }
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            this.store(fos, "Default auto-generated config");
            System.out.println("Default config created at: " + configFile.getAbsolutePath());
        }
    }
}
