package sqlgenlib.parcers.pdmparcer.model;

import java.util.ArrayList;
import java.util.List;

import sqlgenlib.core.model.Table;

public class PDMTable extends Table<PDMColumn> {
    public PDMTable(String code, String comment) {
        super(null, code, comment, null, 0);
    }

    public PDMTable(String schemaCode, String code, String comment, List<PDMColumn> columns, int lastMigrationNo) {
        super(schemaCode, code, comment, columns, lastMigrationNo);
    }

    @Override
    public List<PDMColumn> getColumns() {
        return super.getColumns();
    }

    @Override
    public void setColumns(List<PDMColumn> columns) {
        super.setColumns(columns);
    }

    @Override
    public String getCreateScript() {
        throw new UnsupportedOperationException("Unimplemented method 'getCreateScript'");
    }

    @Override
    public String getAddColumnsScript(List<PDMColumn> columns) {
        throw new UnsupportedOperationException("Unimplemented method 'getAddColumnsScript'");
    }

    @Override
    public String getCreatePartitionScript(String partitionName, List<String> values) {
        return "";
    }

    @Override
    public String getDropColumnsScript(List<PDMColumn> columns) {
        return "";
    }

    @Override
    public Table<PDMColumn> copy() {
        List<PDMColumn> copiedColumns = new ArrayList<>();
        for (PDMColumn col : getColumns()) {
            copiedColumns.add(col.copy());
        }

        return new PDMTable(
                getSchemaCode(),
                getCode(),
                getComment(),
                copiedColumns,
                getLastMigrationNo()
        );
    }

}
