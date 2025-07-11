package sqlgen.entity.baseEntity;

import sqlgen.parcers.pdmparcer.pdmsource.PDMColumn;

import java.util.ArrayList;
import java.util.List;

public interface TableBase {

    String code = "";
    List<ColumnBase> columns = new ArrayList<>();


    public default String getCode() {
        return code;
    }

    public default void setCode(String code) {
    }

    public default List getColumns() {
        return columns;
    }

    // Getters and Setters
    public default void addColumn(ColumnBase columnBase) {
        columns.add(columnBase);
    }
}


