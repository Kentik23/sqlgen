package sqlgen.parcers.pdmparcer.model;

import sqlgen.core.model.Column;

public class PDMColumn extends Column {
    public PDMColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        super(code, datatype, defaultValue, comment, mandatory);
    }
}
