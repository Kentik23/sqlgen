/*package sqlgenlib.utils;

import sqlgenlib.generators.clickhouse.model.CHColumn;
import sqlgenlib.generators.clickhouse.model.CHTable;
import sqlgenlib.generators.greenplum.model.GPColumn;
import sqlgenlib.generators.greenplum.model.GPTable;
import sqlgenlib.parcers.pdmparcer.model.PDMColumn;
import sqlgenlib.parcers.pdmparcer.model.PDMTable;

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

    public static GPTable toGP(PDMTable pdmTable) {
        GPTable gpTable = new GPTable(pdmTable);
        List<GPColumn> gpColumns = new LinkedList<>();

        for (PDMColumn pdmColumn : pdmTable.getColumns()) {
            gpColumns.add(new GPColumn(pdmColumn));
        }
        gpTable.setColumns(gpColumns);

        return gpTable;
    }
}*/
