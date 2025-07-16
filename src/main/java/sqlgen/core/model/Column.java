package sqlgen.core.model;


public class Column {
    private String code;
    private String datatype;
    private String defaultValue;
    private String comment;
    private boolean mandatory;

    public Column(String code, String datatype, String defaultValue, String comment, boolean mandatory) {
        this.code = code;
        this.datatype = datatype;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.mandatory = mandatory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
