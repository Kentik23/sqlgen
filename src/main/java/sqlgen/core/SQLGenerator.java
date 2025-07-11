package sqlgen.core;

import java.util.List;

public interface SQLGenerator {
    List<SQLFile> generate();
}
