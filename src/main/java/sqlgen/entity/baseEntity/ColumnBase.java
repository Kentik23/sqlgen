package sqlgen.entity.baseEntity;

public interface ColumnBase {
    String code = "";
    String precision = "";
    String mandatory = "";

    public default void setCode(String code) { }

    public default String getCode() {
        return "";
    }

}
