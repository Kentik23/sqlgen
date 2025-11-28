package sqlgenlib.utils;

import java.util.HashMap;
import java.util.Map;

public class TemplateBuilder {
    private final String template;
    private final Map<String, String> params = new HashMap<>();

    private TemplateBuilder(String template) {
        this.template = template;
    }

    public static TemplateBuilder from(String template) {
        return new TemplateBuilder(template);
    }

    public TemplateBuilder with(String key, String value) {
        params.put(key, value);
        return this;
    }

    public String build() {
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}

// Использование:
// String path = TemplateBuilder.from(template)
//         .with("dbType", dbType)
//         .with("database", database)
//         .with("scheme", schema)
//         .with("table", table)
//         .build();
// Подставит параметры в строку src/main/{dbType}/databases/{database}/schemas/{scheme}/tables/{table}/

