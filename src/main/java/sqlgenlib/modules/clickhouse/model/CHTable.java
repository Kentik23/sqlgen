//package sqlgenlib.modules.clickhouse.model;
//
//import sqlgenlib.core.model.Column;
//import sqlgenlib.core.model.Table;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CHTable extends Table <CHColumn> {
//    private boolean withDistributed;
//
//    public CHTable() {
//
//    }
//
//    public CHTable(String schemaCode, String code, String comment, List<CHColumn> columns, int lastMigrationNo, boolean withDistributed) {
//        super(schemaCode, code, comment, columns, lastMigrationNo);
//        this.withDistributed = withDistributed;
//    }
//
//    public CHTable(String schemaCode, String code, String comment, List<CHColumn> columns, int lastMigrationNo) {
//        super(schemaCode, code, comment, columns, lastMigrationNo);
//    }
//
//    public CHTable(Table<? extends Column> table) {
//        super(table);
//    }
//
//
//
//    @Override
//    public String getReinitScript(Table<CHColumn> oldTable) {
//        return "";
//    }
//
//    @Override
//    public String getAddColumnsScript(List<CHColumn> columns) {
//        StringBuilder sb = new StringBuilder();
//
//        String database = this.getSchemaCode();
//        String table = this.getCode();
//
//        for (Column column : columns) {
//            sb.append("alter table ")
//                    .append(database).append('.').append(table)
//                    .append(" on cluster main ")
//                    .append("add column ")
//                    .append(column.getCode()).append(' ');
//
//            // Тип с учётом nullable
//            if (column.isMandatory()) {
//                sb.append(column.getDatatype());
//            } else {
//                sb.append("Nullable(").append(column.getDatatype()).append(')');
//            }
//
//            // Default
//            if (column.getDefaultValue() != null && !column.getDefaultValue().isEmpty()) {
//                sb.append(" default ").append(column.getDefaultValue());
//            }
//
//            // Comment
//            if (column.getComment() != null && !column.getComment().isEmpty()) {
//                String safeComment = column.getComment().replace("'", "\\'");
//                sb.append(" comment '").append(safeComment).append('\'');
//            }
//
//            sb.append(';').append('\n');
//
//            // Роллбэк
//            sb.append("--rollback alter table ")
//                    .append(database).append('.').append(table)
//                    .append(" on cluster main ")
//                    .append("drop column ")
//                    .append(column.getCode()).append(';').append('\n').append('\n');
//        }
//
//        return sb.toString();
//    }
//
//    @Override
//    public String getCreatePartitionScript(String partitionName, List<String> values) {
//        return "";
//    }
//
//    @Override
//    public String getDropColumnsScript(List<CHColumn> columns) {
//        return "";
//    }
//
//    @Override
//    public CHTable copy() {
//        List<CHColumn> copiedColumns = new ArrayList<>();
//        for (CHColumn col : getColumns()) {
//            copiedColumns.add(col.copy());
//        }
//
//        return new CHTable(
//                getSchemaCode(),
//                getCode(),
//                getComment(),
//                copiedColumns,
//                getLastMigrationNo(),
//                isWithDistributed()
//        );
//    }
//
//    public void setWithDistributed(boolean withDistributed) {
//        this.withDistributed = withDistributed;
//    }
//
//    public boolean isWithDistributed() {
//        return withDistributed;
//    }
//}
