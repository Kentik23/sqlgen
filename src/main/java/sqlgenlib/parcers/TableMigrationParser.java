package sqlgenlib.parcers;

import sqlgenlib.core.io.SQLFile;

import java.util.List;

public abstract class TableMigrationParser<T> {
    public abstract T parseTable(List<SQLFile> sqlFiles);
}
