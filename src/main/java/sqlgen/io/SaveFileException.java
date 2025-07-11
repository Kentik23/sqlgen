package sqlgen.io;

import java.io.IOException;

public class SaveFileException extends IOException {
    public SaveFileException(IOException cause) {
        super(cause);
    }
}
