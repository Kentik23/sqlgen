package sqlgen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class AppConfig extends Properties {
    private static final String APP_DIR = System.getProperty("user.dir");
    private static final String RELATIVE_CONFIG_PATH = "/config/app.properties";

    private static final Map<String, String> DEFAULT_KEYS = new HashMap<>();

    static {
        DEFAULT_KEYS.put("project.config", "config/projects/example-project-config.yaml");
        DEFAULT_KEYS.put("task.config", "config/tasks/example-task-config.yaml");
        DEFAULT_KEYS.put("clickhouse.config", "config/clickhouse.yaml");
        DEFAULT_KEYS.put("greenplum.config", "config/greenplum.yaml");
        DEFAULT_KEYS.put("postgresql.config", "config/postgresql.yaml");
    }

    private final ProjectConfig projectConfig;
    
    public ProjectConfig getProjectConfig() {
        return projectConfig;
    }

    private final TaskConfig taskConfig;

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public AppConfig() throws IOException {
        this.parseConfig();
        this.projectConfig = this.parseProjectConfig(this.getProperty("project.config"));
        this.taskConfig = this.parseTaskConfig(this.getProperty("task.config"));
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

    private ProjectConfig parseProjectConfig(String path) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(inputStream, ProjectConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ProjectConfig from YAML", e);
        }
    }

    private TaskConfig parseTaskConfig(String path) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(inputStream, TaskConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load TaskConfig from YAML", e);
        }
    }
}
