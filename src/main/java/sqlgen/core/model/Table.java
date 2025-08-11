package sqlgen.core.model;

import java.util.List;

public abstract class Table<C extends Column> {
    private Schema schema;
    private String code;
    private String comment;
    private List<C> columns;
    private int lastMigrationNo;

    public Table() {
    }

    public Table(Schema schema, String code, String comment, List<C> columns, int lastMigrationNo) {
        this.schema = schema;
        this.code = code;
        this.comment = comment;
        this.columns = columns;
        this.lastMigrationNo = lastMigrationNo;
    }

    public Table(Table<? extends Column> table) {
        this.schema = table.getSchema();
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

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public int getLastMigrationNo() {
        return lastMigrationNo;
    }

    public void setLastMigrationNo(int lastMigrationNo) {
        this.lastMigrationNo = lastMigrationNo;
    }

    public abstract String getCreateScript();

    public abstract String getAddColumnsScript(List<C> columns);
}
