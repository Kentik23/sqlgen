package sqlgen.entity.pdmsource;

public class Column {
    private String id;
    private String name;
    private String code;
    private String dataType;
    private String length;
    private String precision;
    private String mandatory;
    private String comment;
    private boolean primaryKey;

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getCode() {
        return code;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return "\n  Column{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", length=" + length +
                ", PK=" + primaryKey +
                '}';
    }

    public void printCode(){
        System.out.println(getCode());
    }
}
