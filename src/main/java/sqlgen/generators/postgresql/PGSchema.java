package sqlgen.generators.postgresql;

import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class PGSchema extends Schema {
    public PGSchema(String code, List<Table> tables) {
        super(code, tables);
    }
}
