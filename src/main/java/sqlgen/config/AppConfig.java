package sqlgen.config;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private String configPath;
    private Map<String, String> keyConfig = new HashMap<>();

    public AppConfig(String configPath) {
        this.configPath = configPath;
    }

    public String getSaveFolder() {
        return keyConfig.get("save_path");
    }

    private void parseConfig() {

    }


}
