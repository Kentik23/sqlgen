package sqlgen.entity;

import java.util.List;

public interface SQLGenerator {
    List<SQLFile> generate();
}
