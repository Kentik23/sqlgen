package sqlgen.generators.clickhouse.model;

import sqlgen.core.model.Column;

public class CHColumn extends Column {
    public CHColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        super(code, datatype, defaultValue, comment, mandatory);
    }
    public CHColumn(Column column) {
        super(column);
    }
}
