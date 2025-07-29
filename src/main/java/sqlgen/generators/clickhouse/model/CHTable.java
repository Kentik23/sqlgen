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
        StringBuilder sb = new StringBuilder();
    
        String database = this.getSchema().getCode();
        String table = this.getCode();
        List<Column> columns = this.getColumns();
    
        // Вычисление максимальной длины для выравнивания
        int maxNameLen = 0;
        int maxTypeLen = 0;
    
        for (Column col : columns) {
            int nameLen = col.getCode().length();
            int typeLen = col.isMandatory() ? col.getDatatype().length()
                    : ("Nullable(" + col.getDatatype() + ")").length();
    
            if (nameLen > maxNameLen) maxNameLen = nameLen;
            if (typeLen > maxTypeLen) maxTypeLen = typeLen;
        }
    
        sb.append("create table if not exists ")
          .append(database).append('.').append(table)
          .append(" on cluster main (\n");
    
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
    
            String name = col.getCode();
            String type = col.isMandatory()
                    ? col.getDatatype()
                    : "Nullable(" + col.getDatatype() + ")";
            String defaultVal = col.getDefaultValue();
            String comment = col.getComment();
    
            sb.append("    ")
              .append(String.format("%-" + maxNameLen + "s", name)).append("   ")
              .append(String.format("%-" + maxTypeLen + "s", type));
    
            if (defaultVal != null) {
                sb.append("   default ").append(defaultVal);
            }
    
            if (comment != null && !comment.isEmpty()) {
                String safeComment = comment.replace("'", "\\'");
                sb.append("   comment '").append(safeComment).append('\'');
            }
    
            if (i < columns.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }
    
        sb.append(")\n")
          .append("engine = MergeTree()\n")
          .append("order by tuple();\n\n");
    
        // Роллбэк
        sb.append("--rollback drop table if exists ")
          .append(database).append('.').append(table)
          .append(" on cluster main;\n");
    
        return sb.toString();
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
