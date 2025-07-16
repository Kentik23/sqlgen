package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Column;

public class GPColumn extends Column {
    public GPColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        super(code, datatype, defaultValue, comment, mandatory);
    }
}
