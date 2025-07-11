package sqlgen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

public class AppConfig extends Properties {
    private static final String APP_DIR = System.getProperty("user.dir");
    private static final String RELATIVE_CONFIG_PATH = "/config/app.properties";

    private static final Map<String, String> DEFAULT_KEYS = new HashMap<>();

    static {
        DEFAULT_KEYS.put("test", "default_test_value");
        DEFAULT_KEYS.put("clickhouse.config", "config/clickhouse.yaml");
        DEFAULT_KEYS.put("greenplum.config", "config/greenplum.yaml");
        DEFAULT_KEYS.put("postgresql.config", "config/postgresql.yaml");
    }

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
        // Задаём все пары ключ-значение из DEFAULT_KEYS
        for (Map.Entry<String, String> entry : DEFAULT_KEYS.entrySet()) {
            this.setProperty(entry.getKey(), entry.getValue());
        }

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
