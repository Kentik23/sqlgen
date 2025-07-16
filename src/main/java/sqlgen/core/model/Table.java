package sqlgen.core.model;

import java.util.List;

public abstract class Table {
    private Schema schema;
    private String code;
    private String comment;
    private List<Column> columns;

    public Table(String code, String comment) {
        this.code = code;
        this.comment = comment;
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

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public abstract String getCreateScript();

    public abstract String getAddColumnsScript(List<Column> columns);
}
