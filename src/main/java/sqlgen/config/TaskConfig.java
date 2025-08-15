package sqlgen.config;

import java.util.List;

public record TaskConfig(
        String username,
        String taskNo,
        String schema,
        boolean generateChangelogs,
        boolean generateSTGTables,
        boolean generateDistributedTables,
        List<String> tablesList
) {}
