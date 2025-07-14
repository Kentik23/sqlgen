package sqlgen.core.model;

import lombok.Data;

@Data
public class Column {
    private String code;
    private String datatype;
    private String comment;
    private boolean mandatory;
}
