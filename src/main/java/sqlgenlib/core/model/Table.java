package sqlgenlib.core.model;

import java.util.List;

public abstract class Table<C extends Column> {
    private String schemaCode;
    private String code;
    private String comment;
    private List<C> columns;
    private int lastMigrationNo;

    public Table() {
    }

    public Table(String schemaCode, String code, String comment, List<C> columns, int lastMigrationNo) {
        this.schemaCode = schemaCode;
        this.code = code;
        this.comment = comment;
        this.columns = columns;
        this.lastMigrationNo = lastMigrationNo;
    }

    public Table(Table<? extends Column> table) {
        this.schemaCode = table.getSchemaCode();
        this.code = table.getCode();
        this.comment = table.getComment();
        this.columns = null;
        this.lastMigrationNo = table.getLastMigrationNo();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<C> getColumns() {
        return columns;
    }

    public void setColumns(List<C> columns) {
        this.columns = columns;
    }

    public int getLastMigrationNo() {
        return lastMigrationNo;
    }

    public void setLastMigrationNo(int lastMigrationNo) {
        this.lastMigrationNo = lastMigrationNo;
    }

    public abstract String getCreateScript();

    public abstract String getAddColumnsScript(List<C> columns);

    public abstract String getCreatePartitionScript(String partitionName, List<String> values);

    public abstract String getDropColumnsScript(List<C> columns);

    public String getSchemaCode() {
        return schemaCode;
    }

    public void setSchemaCode(String schemaCode) {
        this.schemaCode = schemaCode;
    }
}
