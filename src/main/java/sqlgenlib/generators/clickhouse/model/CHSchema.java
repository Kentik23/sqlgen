package sqlgenlib.generators.clickhouse.model;

import sqlgenlib.core.model.Schema;

import java.util.List;

public class CHSchema extends Schema<CHTable> {
    public CHSchema(String code, List<CHTable> tables) {
        super(code, tables);
    }
}
