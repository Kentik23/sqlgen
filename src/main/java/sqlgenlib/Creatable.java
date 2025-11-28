package sqlgenlib;

public interface Creatable {
    String getCreateTableScript();
    String getDropTableScript();
    String getRenameTableScript();
}
