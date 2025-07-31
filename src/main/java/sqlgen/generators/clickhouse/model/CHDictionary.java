package sqlgen.generators.clickhouse.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.ArrayList;
import java.util.List;

public class CHDictionary extends Table {

    private CHColumn primaryKey;
    private String sourceDatabase;
    private String sourceTable;

    public CHDictionary() {}

    public CHDictionary(
            Schema schema,
            String code,
            String comment,
            List<Column> columns,
            int lastMigrationNo,
            CHColumn primaryKey,
            String sourceDatabase,
            String sourceTable
    ) {
        super(schema, code, comment, new ArrayList<>(columns), lastMigrationNo);
        this.getColumns().addFirst(primaryKey);
        this.primaryKey = primaryKey;
        this.sourceDatabase = sourceDatabase;
        this.sourceTable = sourceTable;
    }

    public CHColumn getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(CHColumn primaryKey) {
        this.primaryKey = primaryKey;
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

        String schema = getSchema().getCode();
        String dictName = getCode();
        List<sqlgen.core.model.Column> columns = getColumns();

        sb.append("create or replace dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main\n(\n");

        for (int i = 0; i < columns.size(); i++) {
            sqlgen.core.model.Column col = columns.get(i);
            sb.append("    ")
                    .append(col.getCode()).append(' ')
                    .append(col.getDatatype());
            if (i < columns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }

        sb.append(")\n");

        if (primaryKey != null) {
            sb.append("primary key ").append(primaryKey.getCode()).append('\n');
        }

        sb.append("source(clickhouse(DB '")
                .append(sourceDatabase)
                .append("' table '")
                .append(sourceTable)
                .append("'))\n");

        sb.append("layout(hashed())\n")
                .append("lifetime(14400);\n\n");

        sb.append("--rollback drop dictionary ")
                .append(schema).append('.').append(dictName)
                .append(" on cluster main;\n");

        return sb.toString();
    }

    @Override
    public String getAddColumnsScript(List<sqlgen.core.model.Column> columns) {
        // Словари не поддерживают ALTER ADD COLUMN
        return "";
    }
}
