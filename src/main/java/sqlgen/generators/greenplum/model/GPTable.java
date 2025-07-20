package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class GPTable extends Table {
    public GPTable() {
        
    }

    public GPTable(Schema schema, String code, String comment, List<Column> columns, int lastMigrationNo) {
        super(schema, code, code, columns, lastMigrationNo);
    }
    
    @Override
    public String getCreateScript() {
        return "";
    }

    @Override
    public String getAddColumnsScript(List<Column> columns) {
        StringBuilder sb = new StringBuilder();
    
        String schema = this.getSchema().getCode();
        String table = this.getCode();
    
        for (Column column : columns) {
            // основной alter table
            sb.append("alter table ")
                    .append(schema).append('.').append(table)
                    .append(" add column ")
                    .append(column.getCode()).append(' ')
                    .append(column.getDatatype());
    
            if (column.isMandatory()) {
                sb.append(" not null");
            }
    
            if (column.getDefaultValue() != null) {
                sb.append(" default ").append(column.getDefaultValue());
            }
    
            sb.append(';').append('\n');
    
            // комментарий, если есть
            if (column.getComment() != null && !column.getComment().isEmpty()) {
                String safeComment = column.getComment().replace("'", "''");
                sb.append("comment on column ")
                        .append(schema).append('.').append(table).append('.')
                        .append(column.getCode())
                        .append(" is '").append(safeComment).append("';")
                        .append('\n');
            }
    
            // откат
            sb.append("--rollback alter table ")
                    .append(schema).append('.').append(table)
                    .append(" drop column ")
                    .append(column.getCode()).append(';')
                    .append('\n').append('\n');
        }
    
        return sb.toString();
    }
    
    
}
