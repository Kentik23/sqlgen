package sqlgen.generators.clickhouse.model;

import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;

import java.util.List;

public class CHSchema extends Schema {
    public CHSchema(String code, List<Table> tables) {
        super(code, tables);
    }
}
