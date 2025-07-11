package sqlgen.entity.pdmsource;

import java.util.ArrayList;
import java.util.List;
import sqlgen.entity.pdmsource.Column;

public class Table {
    private String id;
    private String name;
    private String code;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;
    private final List<Column> columns = new ArrayList<>();

    public List<Column> getColumns() {
        return columns;
    }

    // Getters and Setters
    public void addColumn(Column column) {
        columns.add(column);
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", columns=" + columns.size() +
                '}';
    }
}
