package sqlgenlib.core.model;

import java.util.List;

public class Schema<T extends Table<? extends Column>> {
    private String code;
    private List<T> tables;

    public Schema(String code, List<T> tables) {
        this.code = code;
        this.tables = tables;
        for (T table : tables) {
            table.setSchemaCode(this.getCode());
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<T> getTables() {
        return tables;
    }

    public void setTables(List<T> tables) {
        this.tables = tables;
    }
}
