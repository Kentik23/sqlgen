//package sqlgenlib.generators.clickhouse;
//
//import sqlgenlib.core.io.SQLFile;
//import sqlgenlib.generators.clickhouse.model.CHColumn;
//import sqlgenlib.generators.clickhouse.model.CHTable;
//import sqlgenlib.parcers.TableMigrationParser;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class CHTableMigrationParser extends TableMigrationParser<CHTable> {
//
//    @Override
//    public CHTable parseTable(List<SQLFile> sqlFiles) {
//        // сортируем по имени файла (чтобы 1-0-0 шла раньше 2-0-0)
//        sqlFiles.sort(Comparator.comparing(SQLFile::getRelativePath));
//
//        CHTable current = null;
//
//        for (SQLFile file : sqlFiles) {
//            String content = file.getContent();
//
//            if (containsCreateTable(content)) {
//                current = parseCreate(file.getContent(), file.getRelativePath());
//            }
//            else if (containsDropTable(content)) {
//                // по-хорошему надо обнулить, но чаще drop идёт перед create
//                current = null;
//            }
//            else if (containsReinitTable(content)) {
//                // для reinit просто парсим новый create, как будто заново
//                current = parseCreate(file.getContent(), file.getRelativePath());
//            }
//        }
//
//        return current;
//    }
//
//    private boolean containsCreateTable(String content) {
//        String lowerContent = content.toLowerCase(Locale.ROOT);
//        return lowerContent.contains("create table");
//    }
//
//    private boolean containsDropTable(String content) {
//        return content.toLowerCase(Locale.ROOT).contains("drop table");
//    }
//
//    private boolean containsReinitTable(String content) {
//        return content.toLowerCase(Locale.ROOT).contains("reinit-table");
//    }
//
//    private CHTable parseCreate(String sql, String fileName) {
//        // Удаляем комментарии Liquibase и rollback
//        sql = removeLiquibaseComments(sql);
//
//        // schema.table_name
//        Pattern dictPattern = Pattern.compile(
//                "create\\s+table\\s+if\\s+not\\s+exists\\s+([a-z0-9_]+)\\.([a-z0-9_]+)",
//                Pattern.CASE_INSENSITIVE
//        );
//        Matcher m = dictPattern.matcher(sql);
//        String schema = null, tableName = null;
//        if (m.find()) {
//            schema = m.group(1);
//            tableName = m.group(2);
//        }
//
//        // Вырезаем блок с колонками - улучшенное регулярное выражение
//        Pattern colsBlockPattern = Pattern.compile(
//                "\\(([\\s\\S]*?)\\)\\s*ENGINE\\s*=",
//                Pattern.CASE_INSENSITIVE
//        );
//        Matcher blockMatcher = colsBlockPattern.matcher(sql);
//
//        List<CHColumn> columns = new ArrayList<>();
//        if (blockMatcher.find()) {
//            String columnsSQL = blockMatcher.group(1).trim();
//
//            // Разбиваем на строки и парсим каждую колонку отдельно
//            String[] columnLines = columnsSQL.split("\\n");
//
//            for (String line : columnLines) {
//                line = line.trim();
//                if (line.isEmpty() || line.startsWith("--")) {
//                    continue;
//                }
//
//                // Убираем запятые в конце строки
//                if (line.endsWith(",")) {
//                    line = line.substring(0, line.length() - 1).trim();
//                }
//
//                // Парсим колонку: имя тип [comment 'текст']
//                Pattern colPattern = Pattern.compile(
//                        "([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+(?:\\([^)]*\\))?(?:\\s+Nullable\\([^)]*\\))?)(?:\\s+comment\\s+'([^']*)')?",
//                        Pattern.CASE_INSENSITIVE
//                );
//                Matcher cm = colPattern.matcher(line);
//
//                if (cm.find()) {
//                    String colName = cm.group(1);
//                    String colType = cm.group(2);
//                    String colComment = cm.group(3);
//
//                    columns.add(new CHColumn(
//                            colName,
//                            colType,
//                            null,   // default value
//                            colComment, // comment
//                            false,  // nullable
//                            false   // primary key - будет установлено позже
//                    ));
//                }
//            }
//        }
//
//        // primary key из ORDER BY
//        String pk = null;
//        Pattern pkPattern = Pattern.compile("ORDER BY\\s*\\(([^)]+)\\)", Pattern.CASE_INSENSITIVE);
//        Matcher pkMatcher = pkPattern.matcher(sql);
//        if (pkMatcher.find()) {
//            pk = pkMatcher.group(1).trim();
//        }
//
//        // source DB + table (если есть в будущем)
//        String sourceDb = null, sourceTable = null;
//
//        // migration number
//        int migrationNo = extractMigrationNo(fileName);
//
//        // Создаем таблицу
//        CHTable table = new CHTable(
//                schema,
//                tableName,
//                null, // comment
//                columns,
//                migrationNo,
//                false
//        );
//
//        // Устанавливаем первичные ключи
//        if (pk != null) {
//            String[] pkColumns = pk.split("\\s*,\\s*");
//            for (String pkCol : pkColumns) {
//                for (CHColumn column : columns) {
//                    if (column.getCode().equalsIgnoreCase(pkCol.trim())) {
//                        column.setPrimary(true);
//                    }
//                }
//            }
//        }
//
//        return table;
//    }
//
//    private String removeLiquibaseComments(String sql) {
//        // Удаляем блоки Liquibase comments
//        sql = sql.replaceAll("--liquibase formatted sql[\\s\\S]*?--changeset[^\\n]*", "");
//        sql = sql.replaceAll("--rollback[^\\n]*", "");
//        sql = sql.replaceAll("--\\s*rollback", "");
//        sql = sql.replaceAll("--changeset[^\\n]*", "");
//
//        // Удаляем одиночные комментарии
//        String[] lines = sql.split("\\n");
//        StringBuilder result = new StringBuilder();
//
//        for (String line : lines) {
//            if (!line.trim().startsWith("--")) {
//                result.append(line).append("\n");
//            }
//        }
//
//        return result.toString();
//    }
//
//    private int extractMigrationNo(String fileName) {
//        Matcher m = Pattern.compile("(\\d+)-\\d+-\\d+").matcher(fileName);
//        if (m.find()) {
//            return Integer.parseInt(m.group(1));
//        }
//        return 0;
//    }
//}