package sqlgenlib.modules.clickhouse.model;

public class CHColumn {
    private String code;
    private String datatype;
    private String defaultValue;
    private String comment;
    private boolean mandatory;
    private boolean primary;

    public CHColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory, boolean primary) {
        this.code = code;
        this.datatype = datatype;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.mandatory = mandatory;
        this.primary = primary;
    }

    public CHColumn(CHColumn column) {
        this.code = column.getCode();
        this.datatype = column.getDatatype();
        this.defaultValue = column.getDefaultValue();
        this.comment = column.getComment();
        this.mandatory = column.isMandatory();
        this.primary = column.isPrimary();
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
