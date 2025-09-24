package sqlgenlib.parcers;

import sqlgenlib.core.io.SQLFile;
import sqlgenlib.core.model.Column;
import sqlgenlib.core.model.Table;

import java.util.List;

public abstract class TableMigrationParser<T extends Table<? extends Column>> {

    public abstract T parseTable(List<SQLFile> sqlFiles);
}
