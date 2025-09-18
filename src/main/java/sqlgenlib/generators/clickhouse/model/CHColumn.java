package sqlgenlib.generators.clickhouse.model;

import sqlgenlib.core.model.Column;

public class CHColumn extends Column {
    public CHColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory, boolean primary) {
        super(code, datatype, defaultValue, comment, mandatory, primary);
    }
    public CHColumn(Column column) {
        super(column);
    }
}
