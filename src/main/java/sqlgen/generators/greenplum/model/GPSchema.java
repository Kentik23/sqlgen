package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class GPSchema extends Schema<GPTable> {
    public GPSchema(String code, List<GPTable> tables) {
        super(code, tables);
    }
}
