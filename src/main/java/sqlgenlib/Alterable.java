package sqlgenlib;

public interface Alterable {
    String getRenameScript();
    String getAddColumnScript();
    String getDropColumnScript();
    String getRenameColumnScript();
    String getAddCommentToColumnScript();

}
