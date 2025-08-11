package sqlgen.utils;

import sqlgen.generators.clickhouse.model.CHColumn;
import sqlgen.generators.clickhouse.model.CHTable;
import sqlgen.parcers.pdmparcer.model.PDMColumn;
import sqlgen.parcers.pdmparcer.model.PDMTable;

import java.util.LinkedList;
import java.util.List;

public class TableConverter {
    public static CHTable toCH(PDMTable pdmTable) {
        CHTable chTable = new CHTable(pdmTable);
        List<CHColumn> chColumns = new LinkedList<>();
        
        for (PDMColumn pdmColumn : pdmTable.getColumns()) {
            chColumns.add(new CHColumn(pdmColumn));
        }
        chTable.setColumns(chColumns);

        return chTable;
    }
}
