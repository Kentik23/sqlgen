package sqlgen.parcers.pdmparcer.pdmsource;

import sqlgen.entity.baseEntity.ColumnBase;

public class PDMColumn implements ColumnBase {
    private String code;
    private String dataType;
    private String length;
    private String precision;
    private String mandatory;
    private String comment;

    public void setCode(String code) {
        this.code = code;
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



    @Override
    public String toString() {
        return "\n  Column{" +
                ", code='" + code + '\'' +
                ", length=" + length +
                '}';
    }

    public void printCode(){
        System.out.println(getCode());
    }
}