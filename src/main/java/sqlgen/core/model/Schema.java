package sqlgen.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Schema {
    private String code;
    private List<Table> tables;
}
