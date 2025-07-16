package sqlgen.generators.greenplum.model;

import sqlgen.core.model.Column;
import sqlgen.core.model.Table;

import java.util.List;

public class GPTable extends Table {
    public GPTable(String code, String comment) {
        super(code, comment);
    }

    @Override
    public String getCreateScript() {
        return "";
    }

    @Override
    public String getAddColumnsScript(List<Column> columns) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Column column : columns) {
            stringBuilder.append("alter table ").append(this.getSchema().getCode()).append('.')
                    .append(this.getCode()).append(" add column ").append(column.getCode()).append(' ')
                    .append(column.getDatatype()).append(' ').append(column.isMandatory() ? "not null " : "null ")
                    .append("default ").append(column.getDefaultValue()).append(';').append('\n');
            stringBuilder.append("--rollback alter table ").append(this.getSchema().getCode()).append('.')
                    .append(this.getCode()).append(" drop column ").append(column.getCode()).append(';').append('\n').append('\n');
        }
        return stringBuilder.toString();
    }
}
