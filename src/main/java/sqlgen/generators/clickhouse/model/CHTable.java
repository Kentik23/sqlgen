package sqlgen.generators.clickhouse.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class CHTable extends Table {
    public CHTable() {
        
    }

    public CHTable(Schema schema, String code, String comment, List<Column> columns, int lastMigrationNo) {
        super(schema, code, code, columns, lastMigrationNo);
    }
    
    @Override
    public String getCreateScript() {
        return "";
    }

    @Override
    public String getAddColumnsScript(List<Column> columns) {
        StringBuilder sb = new StringBuilder();

        String database = this.getSchema().getCode();
        String table = this.getCode();

        for (Column column : columns) {
            sb.append("alter table ")
                    .append(database).append('.').append(table)
                    .append(" on cluster main ")
                    .append("add column ")
                    .append(column.getCode()).append(' ');

            // Тип с учётом nullable
            if (column.isMandatory()) {
                sb.append(column.getDatatype());
            } else {
                sb.append("Nullable(").append(column.getDatatype()).append(')');
            }

            // Default
            if (column.getDefaultValue() != null) {
                sb.append(" default ").append(column.getDefaultValue());
            }

            // Comment
            if (column.getComment() != null && !column.getComment().isEmpty()) {
                String safeComment = column.getComment().replace("'", "\\'");
                sb.append(" comment '").append(safeComment).append('\'');
            }

            sb.append(';').append('\n');

            // Роллбэк
            sb.append("--rollback alter table ")
                    .append(database).append('.').append(table)
                    .append(" on cluster main ")
                    .append("drop column ")
                    .append(column.getCode()).append(';').append('\n').append('\n');
        }

        return sb.toString();
    }
}
