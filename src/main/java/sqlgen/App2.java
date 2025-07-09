package sqlgen;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class App2 {

    private static final String USERNAME = "aleandivanov";
    private static final String TASK_NO = "GISMUBI-27988";
    private static final String SCHEMA_NAME = "dm_ml";

    private static final Map<String, String> POSTFIX_DEFAULTS = new HashMap<>();
    private static final Map<String, String> TYPE_ALIASES = new HashMap<>();
    private static final Map<String, String> DISTRIBUTION_KEYS = new HashMap<>();

    static {
        POSTFIX_DEFAULTS.put("_id", "not null default -1");
        POSTFIX_DEFAULTS.put("_no", "not null default 'N/D'");
        POSTFIX_DEFAULTS.put("_dttm", "not null default now()");
        POSTFIX_DEFAULTS.put("_name", "not null default 'Не определено'");
        POSTFIX_DEFAULTS.put("_code", "not null default 'N/D'");
        POSTFIX_DEFAULTS.put("_ind", "not null default 'X'");
        POSTFIX_DEFAULTS.put("_qnt", "not null default 0");
        POSTFIX_DEFAULTS.put("_uid", "not null default '00000000-0000-0000-0000-000000000000'");
        POSTFIX_DEFAULTS.put("_bool", "not null default false");

        TYPE_ALIASES.put("int8", "bigint");
        TYPE_ALIASES.put("INT8", "BIGINT");
        TYPE_ALIASES.put("int4", "integer");
        TYPE_ALIASES.put("INT4", "INTEGER");

        DISTRIBUTION_KEYS.put("dim_elap_provision_hist", "dds");
    }

    public static String getHeader(String username, String taskNo) {
        return "--liquibase formatted sql\n"
                + "--changeset " + username + ":1-0-0-CD-" + taskNo + "-init.sql runInTransaction:true\n";
    }

    public static String getRollback(String schema, String table) {
        return "--rollback drop table " + schema + "." + table + ";";
    }

    public static String padRight(String text, int length) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) sb.append(' ');
        return sb.toString();
    }

    public static String getDefaultForColumn(String columnName) {
        for (Map.Entry<String, String> entry : POSTFIX_DEFAULTS.entrySet()) {
            if (columnName.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "";
    }

    public static String applyTypeAlias(String originalType) {
        return TYPE_ALIASES.getOrDefault(originalType, originalType);
    }

    public static void main(String[] args) {
        String inputPdm = "C:\\Users\\aleandivanov\\dit-ex\\dwh\\model\\pd\\PhysModel_PG_Schemas.pdm";
        List<String> neededTables = List.of("dim_elap_provision_hist");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(inputPdm));

            NodeList tableNodes = doc.getElementsByTagNameNS("*", "Table");

            for (int i = 0; i < tableNodes.getLength(); i++) {
                Element table = (Element) tableNodes.item(i);

                String tableCode = getTagValue(table, "Code");

                if (!tableCode.isEmpty()) {
                    System.out.println("Вот же");
                }

                if (!neededTables.contains(tableCode)) continue;

                String distKey = DISTRIBUTION_KEYS.getOrDefault(tableCode, "id");
                String outputDir = tableCode;
                Files.createDirectories(Path.of(outputDir));
                String outputFileName = "1-0-0-CD-" + TASK_NO + "-init.sql";

                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(outputDir + "/" + outputFileName))) {

                    writer.write(getHeader(USERNAME, TASK_NO));
                    writer.newLine();

                    writer.write("create table " + SCHEMA_NAME + "." + tableCode + " (\n");

                    Element columnsContainer = (Element) table.getElementsByTagNameNS("*", "Columns").item(0);
                    NodeList columns = columnsContainer.getElementsByTagNameNS("*", "Column");

                    for (int j = 0; j < columns.getLength(); j++) {
                        Element column = (Element) columns.item(j);

                        String colName = getTagValue(column, "Code");
                        String rawType = getTagValue(column, "DataType");

                        String type = applyTypeAlias(rawType);
                        String paddedName = padRight(colName, 28);
                        String paddedType = padRight(type, 20);
                        String defaultPart = getDefaultForColumn(colName);

                        if (j > 0) writer.write(",\n");
                        writer.write("    " + paddedName + paddedType + defaultPart);
                    }

                    writer.newLine();
                    writer.write(")\n");
                    writer.write("with\n");
                    writer.write("( appendonly = true,\n");
                    writer.write("  orientation = column,\n");
                    writer.write("  compresstype = zstd,\n");
                    writer.write("  compresslevel = 5\n");
                    writer.write(")\n");
                    writer.write("distributed by (" + distKey + ");\n\n");

                    writer.write(getRollback(SCHEMA_NAME, tableCode));
                }
                System.out.println("✅ Сгенерирован файл для таблицы: " + tableCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(Element parent, String localName) {
        NodeList list = parent.getElementsByTagNameNS("*", localName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }
}
