package sqlgen.parcers.pdmparcer.model;

import java.util.List;

import sqlgen.core.model.Table;

public class PDMTable extends Table<PDMColumn> {
    public PDMTable(String code, String comment) {
        super(null, code, comment, null, 0);
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

}
