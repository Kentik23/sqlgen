package sqlgen.core.model;

import java.util.List;

public class Schema {
    private String code;
    private List<Table> tables;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
