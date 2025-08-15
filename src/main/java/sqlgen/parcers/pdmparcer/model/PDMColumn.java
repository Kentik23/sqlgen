package sqlgen.parcers.pdmparcer.model;

import sqlgen.core.model.Column;

public class PDMColumn extends Column {
    private String domainCode;

    public PDMColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory, String domainCode) {
        super(code, datatype, defaultValue, comment, mandatory);
        this.domainCode = domainCode;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }
}
