package sqlgen.generators.postgresql;

import sqlgen.core.model.Column;

public class PGColumn extends Column {
    public PGColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        super(code, datatype, defaultValue, comment, mandatory);
    }
}
