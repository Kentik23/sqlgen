package sqlgenlib.generators.clickhouse.model;

import sqlgenlib.core.model.Table;

import java.util.ArrayList;
import java.util.List;

public class CHDictionary extends Table<CHColumn> {
    private String sourceDatabase;
    private String sourceTable;

    public CHDictionary() {}

    public CHDictionary(
            String schemaCode,
            String code,
            String comment,
            List<CHColumn> columns,
            int lastMigrationNo,
            String sourceDatabase,
            String sourceTable
    ) {
        super(schemaCode, code, comment, new ArrayList<>(columns), lastMigrationNo);
        this.sourceDatabase = sourceDatabase;
        this.sourceTable = sourceTable;
    }

    public String getSourceDatabase() {
        return sourceDatabase;
    }

    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    @Override
    public String getCreateScript() {
        StringBuilder sb = new StringBuilder();

        String schema = getSchemaCode();
        String dictName = getCode();
        List<CHColumn> columns = getColumns();

        sb.append("create or replace dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n(\n");

        for (int i = 0; i < columns.size(); i++) {
            CHColumn col = columns.get(i);
            sb.append("    ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());
            if (i < columns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append(")\n");

        columns.stream().filter(CHColumn::isPrimary).findFirst()
                .ifPresent(chColumn -> sb.append("primary key ").append(chColumn.getCode()).append('\n'));

        sb.append("source(clickhouse(DB '")
                .append(sourceDatabase)
                .append("' table '")
                .append(sourceTable)
                .append("'))\n");

        sb.append("layout(hashed())\n")
                .append("lifetime(14400);\n\n");

        sb.append("--rollback drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main sync;\n");

        return sb.toString();
    }

    @Override
    public String getReinitScript(Table<CHColumn> oldTable) {
        if (!(oldTable instanceof CHDictionary oldDict)) {
            throw new IllegalArgumentException("Expected CHDictionary");
        }

        StringBuilder sb = new StringBuilder();

        String schema = getSchemaCode();
        String dictName = getCode();
        List<CHColumn> columns = getColumns();

        // Drop dictionary (текущий)
        sb.append("drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main sync;\n\n");

        // Create or replace dictionary (текущий)
        sb.append("create or replace dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n(\n");

        for (int i = 0; i < columns.size(); i++) {
            CHColumn col = columns.get(i);
            sb.append("    ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());

            if (col.getComment() != null && !col.getComment().isBlank()) {
                sb.append(" comment '").append(col.getComment().replace("'", "''")).append("'");
            }

            if (i < columns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append(")\n");

        columns.stream().filter(CHColumn::isPrimary).findFirst()
                .ifPresent(chColumn -> sb.append("primary key ").append(chColumn.getCode()).append('\n'));

        sb.append("source(clickhouse(user '${CH_CICD_USER}' password '${CH_CICD_PASSWORD}' DB '")
                .append(sourceDatabase)
                .append("' table '")
                .append(sourceTable)
                .append("'))\n")
                .append("layout(hashed())\n")
                .append("lifetime(14400)");

        if (getComment() != null && !getComment().isBlank()) {
            sb.append("\ncomment '").append(getComment().replace("'", "''")).append("'");
        }

        sb.append(";\n\n");

        // ---------- Rollback block (СТАРЫЙ словарь) ----------
        sb.append("--rollback drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main sync;\n");

        sb.append("--rollback create or replace dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n")
                .append("--rollback (\n");

        List<CHColumn> oldColumns = oldDict.getColumns();
        for (int i = 0; i < oldColumns.size(); i++) {
            CHColumn col = oldColumns.get(i);
            sb.append("--rollback     ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());

            if (col.getComment() != null && !col.getComment().isBlank()) {
                sb.append(" comment '").append(col.getComment().replace("'", "''")).append("'");
            }

            if (i < oldColumns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append("--rollback )\n");

        oldColumns.stream().filter(CHColumn::isPrimary).findFirst()
                .ifPresent(chColumn -> sb.append("--rollback primary key ").append(chColumn.getCode()).append('\n'));

        sb.append("--rollback source(clickhouse(user '${CH_CICD_USER}' password '${CH_CICD_PASSWORD}' DB '")
                .append(oldDict.getSourceDatabase())
                .append("' table '")
                .append(oldDict.getSourceTable())
                .append("'))\n")
                .append("--rollback layout(hashed())\n")
                .append("--rollback lifetime(14400)");

        if (oldDict.getComment() != null && !oldDict.getComment().isBlank()) {
            sb.append("\n--rollback comment '").append(oldDict.getComment().replace("'", "''")).append("'");
        }

        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public CHDictionary copy() {
        // копируем колонки
        List<CHColumn> copiedColumns = new ArrayList<>();
        for (CHColumn col : getColumns()) {
            copiedColumns.add(col.copy()); // предполагаем, что у CHColumn есть метод copy()
        }

        // создаём новый объект CHDictionary с теми же значениями
        CHDictionary copy = new CHDictionary(
                getSchemaCode(),
                getCode(),
                getComment(),
                copiedColumns,
                getLastMigrationNo(),
                sourceDatabase,
                sourceTable
        );

        return copy;
    }

    @Override
    public String getAddColumnsScript(List<CHColumn> columns) {
        // Словари не поддерживают ALTER ADD COLUMN
        return "";
    }

    @Override
    public String getCreatePartitionScript(String partitionName, List<String> values) {
        return "";
    }

    @Override
    public String getDropColumnsScript(List<CHColumn> columns) {
        return "";
    }
}
