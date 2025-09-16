package sqlgenlib.generators.greenplum.model;

import sqlgenlib.core.model.Column;
import sqlgenlib.core.model.Table;

import java.util.List;

public class GPTable extends Table<GPColumn> {
    public GPTable() {
        
    }

    public GPTable(String schemaCode, String code, String comment, List<GPColumn> columns, int lastMigrationNo) {
        super(schemaCode, code, code, columns, lastMigrationNo);
    }

    public GPTable(Table<? extends Column> table) {
        super(table);
    }

    @Override
    public String getCreatePartitionScript(String partitionName, List<String> values) {
        StringBuilder sb = new StringBuilder();

        // полное имя таблицы
        String fullTableName = getSchemaCode() + "." + getCode();

        // add partition
        sb.append("alter table ")
                .append(fullTableName)
                .append(" add partition ")
                .append(partitionName)
                .append(" values (")
                .append(String.join(", ", values))
                .append(") with (appendonly = true, orientation = column, compresstype = zstd, compresslevel = 5);\n");

        // rollback: только drop partition
        sb.append("-- rollback alter table ")
                .append(fullTableName)
                .append(" drop partition ")
                .append(partitionName)
                .append(";\n");

        return sb.toString();
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

        sb.append("create table ")
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

            // Убираем пробелы справа
            int len = sb.length();
            while (len > 0 && Character.isWhitespace(sb.charAt(len - 1))) {
                sb.deleteCharAt(len - 1);
                len--;
            }

            if (i < columns.size() - 1) {
                sb.append(',');
            }

            sb.append('\n');
        }
        sb.append(")\n");
        sb.append(
                """
                    with (
                        appendonly = true,
                        orientation = column,
                        compresstype = zstd,
                        compresslevel = 5
                    )
                    distributed by (Поле id)"""
                ).append(";\n\n");

        // Комментарии
        String tableComment = this.getComment().replace("'", "''");
        sb.append("comment on table ")
                .append(schema).append('.').append(table)
                .append(" is '").append(tableComment).append("';\n");
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
        sb.append("\n--rollback drop table ")
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

    @Override
    public String getDropColumnsScript(List<GPColumn> columns) {
        StringBuilder sb = new StringBuilder();

        String schema = this.getSchemaCode();
        String table = this.getCode();

        for (GPColumn column : columns) {
            // основной alter table на удаление
            sb.append("alter table ")
                    .append(schema).append('.').append(table)
                    .append(" drop column ")
                    .append(column.getCode())
                    .append(";\n");

            // rollback: возвращаем колонку обратно
            sb.append("--rollback alter table ")
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

            sb.append(";\n");

            if (column.getComment() != null && !column.getComment().isEmpty()) {
                String safeComment = column.getComment().replace("'", "''");
                sb.append("--rollback comment on column ")
                        .append(schema).append('.').append(table).append('.')
                        .append(column.getCode())
                        .append(" is '").append(safeComment).append("';\n");
            }

            sb.append('\n');
        }

        return sb.toString();
    }
}
