package sqlgen.parcers;

import sqlgen.config.DBConfig;
import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.generators.greenplum.model.GPTable;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DWHProjectParser {
    public static GPTable parseTable(String tableName, DBConfig config, String path) {
        Path projectPath = Path.of(path);
        Schema schema = new Schema("dm_ap", new ArrayList<>());
        GPTable table = new GPTable(schema, tableName, null, new ArrayList<>(), 0);

        Path tableDir = projectPath.resolve(
                config.tableMigrationPathTemplate()
                        .replace("{schemePath}", config.schemePath())
                        .replace("{scheme}", schema.getCode()) // если используется
                        .replace("{table}", tableName)
                        .replace("{filename}", "") // не нужен
        );

        if (!Files.exists(tableDir)) {
            throw new RuntimeException("Table directory not found: " + tableDir);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tableDir, "*.sql")) {
            for (Path file : sortFilesByMigrationNo(stream)) {
                String sql = Files.readString(file);
                applyMigration(table, sql);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading table migration files", e);
        }

        return table;
    }

    private static List<Path> sortFilesByMigrationNo(DirectoryStream<Path> stream) {
        List<Path> sorted = new ArrayList<>();
        stream.forEach(sorted::add);
        sorted.sort(Comparator.comparing(path -> {
            String fileName = path.getFileName().toString();
            Matcher matcher = Pattern.compile("(\\d+)-(\\d+)-(\\d+)").matcher(fileName);
            if (matcher.find()) {
                return String.format("%03d%03d%03d",
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)));
            }
            return fileName;
        }));
        return sorted;
    }

    private static void applyMigration(GPTable table, String sql) {
        // 1. INIT: create table
        Pattern createPattern = Pattern.compile("create table .*?\\((.*?)\\)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher createMatcher = createPattern.matcher(sql);
        if (createMatcher.find()) {
            String columnsBlock = createMatcher.group(1);
            List<Column> columns = parseColumnBlock(columnsBlock);
            table.setColumns(columns);
        }

        // 2. ADD COLUMN
        Pattern addColumnPattern = Pattern.compile("alter table .*? add column (.*?)\\s+(\\w+)([^;]*);", Pattern.CASE_INSENSITIVE);
        Matcher addColumnMatcher = addColumnPattern.matcher(sql);
        while (addColumnMatcher.find()) {
            String colName = addColumnMatcher.group(1).trim();
            String colType = addColumnMatcher.group(2).trim();
            String tail = addColumnMatcher.group(3).toLowerCase();

            boolean notNull = tail.contains("not null");
            String defaultValue = null;

            Pattern defaultPattern = Pattern.compile("default\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);
            Matcher defMatch = defaultPattern.matcher(tail);
            if (defMatch.find()) {
                defaultValue = defMatch.group(1);
            }

            table.getColumns().add(new Column(colName, colType, defaultValue, "", notNull));
        }

        // 3. COMMENTS
        Pattern commentPattern = Pattern.compile("comment on column .*?\\.(.*?) is '(.*?)';", Pattern.CASE_INSENSITIVE);
        Matcher commentMatcher = commentPattern.matcher(sql);
        while (commentMatcher.find()) {
            String colName = commentMatcher.group(1).trim();
            String comment = commentMatcher.group(2).trim();
            table.getColumns().stream()
                    .filter(c -> c.getCode().equalsIgnoreCase(colName))
                    .findFirst()
                    .ifPresent(c -> c.setComment(comment));
        }
    }

    private static List<Column> parseColumnBlock(String block) {
        List<Column> columns = new ArrayList<>();
        String[] lines = block.split(",\\s*\\n");

        for (String line : lines) {
            line = line.trim().replaceAll("/\\*.*?\\*/", ""); // remove inline comments
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            if (parts.length < 2) continue;

            String name = parts[0];
            String type = parts[1];
            boolean notNull = line.toLowerCase().contains("not null");

            String defaultValue = null;
            Matcher defMatch = Pattern.compile("default\\s+([^\\s,]+)", Pattern.CASE_INSENSITIVE).matcher(line);
            if (defMatch.find()) {
                defaultValue = defMatch.group(1);
            }

            columns.add(new Column(name, type, defaultValue, "", notNull));
        }

        return columns;
    }
}

