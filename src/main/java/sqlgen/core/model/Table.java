package sqlgen.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Table {
    private String code;
    private String comment;
    private List<Column> columns;
}
