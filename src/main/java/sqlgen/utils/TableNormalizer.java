package sqlgen.utils;

import sqlgen.core.model.Column;
import sqlgen.core.model.Domain;
import sqlgen.parcers.pdmparcer.model.PDMColumn;
import sqlgen.parcers.pdmparcer.model.PDMTable;

import java.util.*;

public class TableNormalizer {
    // Порядок полей (Системные поля всегда вверху)
    private final List<String> priority = List.of(
            "src_id",
            "task_id",
            "create_dttm",
            "modify_dttm",
            "task_dttm",
            "eff_dttm",
            "exp_dttm",
            "action_ind"
    );
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

    public void normalize(PDMTable table, boolean applyDefault) throws DomainNotFoundException {
        List<PDMColumn> columns = table.getColumns();
        for (PDMColumn column : columns) {
            normalize(column, applyDefault);
        }
    }

    private void normalize(PDMColumn column, boolean applyDefault) throws DomainNotFoundException {
        column.setDatatype(this.getDomainType(column.getDomainCode()));
        /* TODO отдельная проверка для eff_dttm и exp_dttm, так как у них одинаковый домен
        *   Планируется реализация гибкой настройки исключений*/
        switch (column.getCode()) {
            case "eff_dttm":
                column.setDefaultValue("'1900-01-01 00:00:00+00'");
                break;
            case "exp_dttm":
                column.setDefaultValue("'9999-12-31 00:00:00+00'");
                break;
            default:
                if (applyDefault) column.setDefaultValue(this.getDomainDefault(column.getDomainCode()));
        }
        column.setMandatory(column.getDefaultValue() != null && !column.getDefaultValue().isEmpty());
    }

    public <C extends Column> void sort(List<C> columns) {
        // Для быстрого поиска — мапа с индексом приоритета
        Map<String, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < priority.size(); i++) {
            orderMap.put(priority.get(i), i);
        }

        // Сортировка
        columns.sort(Comparator.comparingInt(c ->
                orderMap.getOrDefault(c.getCode(), Integer.MAX_VALUE)
        ));
    }
}
