package sqlgen.parcers;

import sqlgen.entity.baseEntity.TableBase;
import sqlgen.parcers.pdmparcer.pdmsource.PDMTable;

import java.util.List;

public interface Parcer {

    List<PDMTable> parseFile(String filePath) throws Exception;
}
