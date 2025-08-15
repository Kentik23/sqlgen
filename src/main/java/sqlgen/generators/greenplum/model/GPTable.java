package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Table;

import java.util.List;

public class GPTable extends Table<GPColumn> {
    public GPTable() {
        
    }

    public GPTable(String schemaCode, String code, String comment, List<GPColumn> columns, int lastMigrationNo) {
        super(schemaCode, code, code, columns, lastMigrationNo);
    }
    
    @Override
    public String getCreateScript() {
        StringBuilder sb = new StringBuilder();
    
        String schema = this.getSchemaCode();
        String table = this.getCode();
        List<GPColumn> columns = this.getColumns();
    
        // Вычисление максимальных длин
        int maxNameLen = 0;
        int maxTypeLen = 0;
    
        for (Column col : columns) {
            if (col.getCode().length() > maxNameLen) {
                maxNameLen = col.getCode().length();
            }
            if (col.getDatatype().length() > maxTypeLen) {
                maxTypeLen = col.getDatatype().length();
            }
        }
    
        sb.append("create table if not exists ")
          .append(schema).append('.').append(table)
          .append(" (\n");
    
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            sb.append("    ")
              .append(String.format("%-" + maxNameLen + "s", col.getCode()))
              .append("   ")
              .append(String.format("%-" + maxTypeLen + "s", col.getDatatype()));
    
            if (col.isMandatory()) {
                sb.append("   not null");
            }
    
            if (col.getDefaultValue() != null) {
                sb.append("   default ").append(col.getDefaultValue());
            }
    
            if (i < columns.size() - 1) {
                sb.append(',');
            }
    
            sb.append('\n');
        }
    
        sb.append(");\n\n");
    
        // Комментарии
        for (Column col : columns) {
            if (col.getComment() != null && !col.getComment().isEmpty()) {
                String safeComment = col.getComment().replace("'", "''");
                sb.append("comment on column ")
                  .append(schema).append('.').append(table).append('.')
                  .append(col.getCode())
                  .append(" is '").append(safeComment).append("';\n");
            }
        }
    
        // Откат
        sb.append("\n--rollback drop table if exists ")
          .append(schema).append('.').append(table).append(";\n");
    
        return sb.toString();
    }
    

    @Override
    public String getAddColumnsScript(List<GPColumn> columns) {
        StringBuilder sb = new StringBuilder();
    
        String schema = this.getSchemaCode();
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
