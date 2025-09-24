package sqlgenlib.generators.clickhouse;

import sqlgenlib.core.io.SQLFile;
import sqlgenlib.generators.clickhouse.model.CHColumn;
import sqlgenlib.generators.clickhouse.model.CHDictionary;
import sqlgenlib.parcers.TableMigrationParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CHDictionaryMigrationParser extends TableMigrationParser<CHDictionary> {

    @Override
    public CHDictionary parseTable(List<SQLFile> sqlFiles) {
        // сортируем по имени файла (чтобы 1-0-0 шла раньше 2-0-0)
        sqlFiles.sort(Comparator.comparing(SQLFile::getRelativePath));

        CHDictionary current = null;

        for (SQLFile file : sqlFiles) {
            String content = file.getContent().toLowerCase(Locale.ROOT);

            if (content.contains("create or replace dictionary")) {
                current = parseCreate(file.getContent(), file.getRelativePath());
            }
            else if (content.contains("drop dictionary")) {
                // по-хорошему надо обнулить, но чаще drop идёт перед create
                current = null;
            }
            else if (content.contains("reinit-dictionary")) {
                // для reinit просто парсим новый create, как будто заново
                current = parseCreate(file.getContent(), file.getRelativePath());
            }
            // TODO: сюда можно добавить alter / drop column / add column
        }

        return current;
    }

    private CHDictionary parseCreate(String sql, String fileName) {
        // schema.dictName
        Pattern dictPattern = Pattern.compile("create or replace dictionary\\s+([a-z0-9_]+)\\.([a-z0-9_]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = dictPattern.matcher(sql);
        String schema = null, dictName = null;
        if (m.find()) {
            schema = m.group(1);
            dictName = m.group(2);
        }

        // Вырезаем блок с колонками
        Pattern colsBlockPattern = Pattern.compile(
                "\\((.*?)\\)\\s*primary key",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher blockMatcher = colsBlockPattern.matcher(sql);

        List<CHColumn> columns = new ArrayList<>();
        if (blockMatcher.find()) {
            String columnsSQL = blockMatcher.group(1).trim();

            // Теперь парсим сами колонки
            // Учитываем комментарий: colName Type [comment '...']
            Pattern colPattern = Pattern.compile(
                    "([a-z0-9_]+)\\s+([a-z0-9()]+(?:,[a-z0-9()]+)*)(?:\\s+comment\\s+'([^']*)')?",
                    Pattern.CASE_INSENSITIVE
            );
            Matcher cm = colPattern.matcher(columnsSQL);

            while (cm.find()) {
                String colName = cm.group(1);
                String colType = cm.group(2);
                String colComment = cm.group(3);

                columns.add(new CHColumn(
                        colName,
                        colType,
                        null,   // здесь нормальный коммент
                        colComment,         // placeholder под автора/источник
                        false,
                        false
                ));
            }
        }


        // primary key
        String pk = null;
        Matcher pkM = Pattern.compile("primary key\\s+([a-z0-9_]+)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (pkM.find()) {
            pk = pkM.group(1);
        }

        // source DB + table
        String sourceDb = null, sourceTable = null;
        Matcher srcM = Pattern.compile("db\\s+'([^']+)'\\s+table\\s+'([^']+)'", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (srcM.find()) {
            sourceDb = srcM.group(1);
            sourceTable = srcM.group(2);
        }

        // migration number (можно вытаскивать из имени файла: 1-0-0 …)
        int migrationNo = extractMigrationNo(fileName);

        // собрать словарь
        CHDictionary dict = new CHDictionary(
                schema,
                dictName,
                null, // comment пока пусто
                columns,
                migrationNo,
                sourceDb,
                sourceTable
        );

        if (pk != null) {
            for (CHColumn c : columns) {
                if (c.getCode().equalsIgnoreCase(pk)) {
                    c.setPrimary(true);
                }
            }
        }

        return dict;
    }

    private int extractMigrationNo(String fileName) {
        // примитив: берём первые числа "1-0-0" → 100, "2-0-0" → 200 и т.п.
        Matcher m = Pattern.compile("(\\d+)-(\\d+)-(\\d+)").matcher(fileName);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
}
