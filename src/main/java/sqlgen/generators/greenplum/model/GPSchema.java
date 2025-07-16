package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class GPSchema extends Schema {
    public GPSchema(String code, List<Table> tables) {
        super(code, tables);
    }
}
