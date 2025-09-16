package sqlgenlib.generators.greenplum.model;

import sqlgenlib.core.model.Column;

public class GPColumn extends Column {
    public GPColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        super(code, datatype, defaultValue, comment, mandatory);
    }
    public GPColumn(Column column) {
        super(column);
    }
}
