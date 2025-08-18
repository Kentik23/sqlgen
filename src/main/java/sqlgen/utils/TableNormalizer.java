package sqlgen.utils;

import sqlgen.core.model.Domain;
import sqlgen.parcers.pdmparcer.model.PDMColumn;
import sqlgen.parcers.pdmparcer.model.PDMTable;

import java.util.*;

public class TableNormalizer {
    private final List<Domain> domains;

    public TableNormalizer(List<Domain> domains) {
        this.domains = domains;
    }

    private Optional<Domain> getDomain(String domainCode) {
        return domains.stream().filter(x -> x.code().equals(domainCode)).findFirst();
    }

    private String getDomainType(String domainCode) throws DomainNotFoundException {
        Optional<Domain> domain = getDomain(domainCode);
        if (domain.isPresent())
            return domain.get().type();
        else
            throw new DomainNotFoundException("Domain \"" + domainCode + "\" not found!");
    }

    private String getDomainDefault(String domainCode) throws DomainNotFoundException {
        Optional<Domain> domain = getDomain(domainCode);
        if (domain.isPresent())
            return domain.get().defaultValue();
        else
            throw new DomainNotFoundException("Domain \"" + domainCode + "\" not found!");
    }

    public void normalize(PDMTable table) throws DomainNotFoundException {
        List<PDMColumn> columns = table.getColumns();
        for (PDMColumn column : columns) {
            column.setDatatype(this.getDomainType(column.getDomainCode()));
            column.setDefaultValue(this.getDomainDefault(column.getDomainCode()));
            column.setMandatory(column.getDefaultValue() != null && !column.getDefaultValue().isEmpty());
        }
    }
}
