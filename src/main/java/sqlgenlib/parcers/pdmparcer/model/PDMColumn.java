package sqlgenlib.parcers.pdmparcer.model;

import sqlgenlib.core.model.Column;

public class PDMColumn extends Column {
    private String domainCode;

    public PDMColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory, String domainCode, boolean primary) {
        super(code, datatype, defaultValue, comment, mandatory, primary);
        this.domainCode = domainCode;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }


    @Override
    public PDMColumn copy() {
        return new PDMColumn(
                getCode(),
                getDatatype(),
                getDefaultValue(),
                getComment(),
                isMandatory(),
                getDomainCode(),
                isPrimary()
        );
    }
}
