package sqlgen.generators.clickhouse.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Table;

import java.util.List;

public class CHTable extends Table<CHColumn> {
    private boolean withDistributed;

    public CHTable() {

    }

    public CHTable(String schemaCode, String code, String comment, List<CHColumn> columns, int lastMigrationNo, boolean withDistributed) {
        super(schemaCode, code, comment, columns, lastMigrationNo);
        this.withDistributed = withDistributed;
    }

    public CHTable(String schemaCode, String code, String comment, List<CHColumn> columns, int lastMigrationNo) {
        super(schemaCode, code, comment, columns, lastMigrationNo);
    }

    public CHTable(Table<? extends Column> table) {
        super(table);
    }

    @Override
    public String getCreateScript() {
        StringBuilder sb = new StringBuilder();

        String database = this.getSchemaCode();
        String table = this.getCode();
        List<CHColumn> columns = this.getColumns();
        String tableComment = this.getComment();

        // Вычисляем максимальную ширину
        int maxNameLen = 0;
        int maxTypeLen = 0;
        int maxDefaultLen = 0;

        for (Column col : columns) {
            int nameLen = col.getCode().length();
            int typeLen = col.isMandatory() ? col.getDatatype().length()
                    : ("Nullable(" + col.getDatatype() + ")").length();

            int defaultLen = 0;
            if (col.getDefaultValue() != null && !col.getDefaultValue().isEmpty()) {
                defaultLen = ("default " + col.getDefaultValue()).length();
            }

            maxNameLen = Math.max(maxNameLen, nameLen);
            maxTypeLen = Math.max(maxTypeLen, typeLen);
            maxDefaultLen = Math.max(maxDefaultLen, defaultLen);
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

            if (maxDefaultLen != 0) {
                if (defaultVal != null && !defaultVal.isEmpty()) {
                    sb.append("   ")
                            .append(String.format("%-" + maxDefaultLen + "s", "default " + defaultVal));
                } else {
                    sb.append("   ")
                            .append(String.format("%-" + maxDefaultLen + "s", ""));
                }
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
                .append("engine = ReplicatedMergeTree\n")
                .append("order by(ID таблицы)\n")
                .append("partition by toYYYYMM(dm_dt)");

        if (tableComment != null && !tableComment.isEmpty()) {
            sb.append("\ncomment '").append(tableComment.replace("'", "\\'")).append("'");
        }

        sb.append(";\n\n");

        // Роллбэк
        sb.append("--rollback drop table if exists ")
                .append(database).append('.').append(table)
                .append(" on cluster main;\n");

        // Distributed-таблица
        if (isWithDistributed()) {
            sb.append("\ncreate table if not exists ")
                    .append(database).append('.').append(table).append("_distributed on cluster main\n")
                    .append("as ").append(database).append('.').append(table).append('\n')
                    .append("engine = Distributed('main', '")
                    .append(database).append("', '")
                    .append(table).append("', rand());\n\n");

            sb.append("--rollback drop table if exists ")
                    .append(database).append('.').append(table).append("_distributed on cluster main;\n");
        }

        return sb.toString();
    }

    @Override
    public String getAddColumnsScript(List<CHColumn> columns) {
        StringBuilder sb = new StringBuilder();

        String database = this.getSchemaCode();
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
            if (column.getDefaultValue() != null || !column.getDefaultValue().isEmpty()) {
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

    public void setWithDistributed(boolean withDistributed) {
        this.withDistributed = withDistributed;
    }

    public boolean isWithDistributed() {
        return withDistributed;
    }
}
