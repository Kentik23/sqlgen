package sqlgen.parcers.pdmparcer.pdmsource;

import sqlgen.entity.baseEntity.ColumnBase;
import sqlgen.entity.baseEntity.TableBase;

import java.util.ArrayList;
import java.util.List;

public class PDMTable implements TableBase {
    private String code;
    private List<PDMColumn> columns = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public List<PDMColumn> getColumns() {
        return columns;
    }

    // Getters and Setters
    public void addColumn(PDMColumn column) {
        columns.add(column);
    }

    @Override
    public String toString() {
        return "Table{" +
                ", code='" + code + '\'' +
                ", columns=" + columns.size() +
                '}';
    }
}