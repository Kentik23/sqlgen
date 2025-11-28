//package sqlgenlib.modules.clickhouse;
//
//import sqlgenlib.config.DBConfig;
//import sqlgenlib.config.ProjectConfig;
//import sqlgenlib.config.TaskConfig;
//import sqlgenlib.core.SQLGenerator;
//import sqlgenlib.core.io.SQLFile;
//import sqlgenlib.core.model.Column;
//import sqlgenlib.core.model.Schema;
//import sqlgenlib.core.model.Table;
//import sqlgenlib.generators.clickhouse.model.CHDictionary;
//import sqlgenlib.generators.clickhouse.model.CHSchema;
//import sqlgenlib.generators.clickhouse.model.CHTable;
//import sqlgenlib.modules.clickhouse.model.CHColumn;
//import sqlgenlib.utils.TemplateBuilder;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//
//public class CHSQLGenerator extends SQLGenerator {
//    public CHSQLGenerator(ProjectConfig projectConfig, DBConfig dbConfig, TaskConfig taskConfig) throws IOException {
//        super(projectConfig, dbConfig, taskConfig);
//    }
//
//    public String getColumnExpression(List<CHColumn> columns) {
//        String template = "    {nameWithPadding}{typeWithPadding}{defaultWithPadding}{comment}";
//        StringBuilder expression = new StringBuilder();
//
//        // Вычисляем максимальную ширину
//        int maxNameLen = 0;
//        int maxTypeLen = 0;
//        int maxDefaultLen = 0;
//        for (CHColumn col : columns) {
//            int nameLen = col.getCode().length();
//            int typeLen = col.isMandatory() ? col.getDatatype().length() : col.getDatatype().length() + 10;
//            int defaultLen = col.getDefaultValue() == null ? 0 : col.getDefaultValue().length() + 8;
//
//            maxNameLen = Math.max(maxNameLen, nameLen);
//            maxTypeLen = Math.max(maxTypeLen, typeLen);
//            maxDefaultLen = Math.max(maxDefaultLen, defaultLen);
//        }
//
//        for (CHColumn col : columns) {
//
//            TemplateBuilder.from(template)
//                    .with("nameWithPadding", String.format("%-" + maxNameLen + "s", col.getCode()))
//                    .with("typeWithPadding", String.format("%-" + maxTypeLen + "s", col.isMandatory() ? col.getDatatype() : "Nullable(" + col.getDatatype() + ")"))
//                    .with("defaultWithPadding", String.format("%-" + maxTypeLen + "s", col.getDatatype()))
//
//            String name = col.getCode();
//            String type = col.isMandatory()
//                    ? col.getDatatype()
//                    : "Nullable(" + col.getDatatype() + ")";
//            String defaultVal = col.getDefaultValue();
//            String comment = col.getComment();
//
//            sb.append("    ")
//                    .append(String.format("%-" + maxNameLen + "s", name)).append("   ")
//                    .append(String.format("%-" + maxTypeLen + "s", type));
//
//            if (maxDefaultLen != 0) {
//                if (defaultVal != null && !defaultVal.isEmpty()) {
//                    sb.append("   ")
//                            .append(String.format("%-" + maxDefaultLen + "s", "default " + defaultVal));
//                } else {
//                    sb.append("   ")
//                            .append(String.format("%-" + maxDefaultLen + "s", ""));
//                }
//            }
//
//            if (comment != null && !comment.isEmpty()) {
//                String safeComment = comment.replace("'", "\\'");
//                sb.append("   comment '").append(safeComment).append('\'');
//            }
//
//            if (i < columns.size() - 1) {
//                sb.append(',');
//            }
//            sb.append('\n');
//        }
//    }
//
//    public String getCreateTableScript(CHTable) {
//        String template = """
//                create table {tableName} on cluster main (
//                {columnsExpression}
//                ){engineExpression}{commentTable};
//                """;
//        StringBuilder sb = new StringBuilder();
//
//        String database = this.getSchemaCode();
//        String table = this.getCode();
//        List<CHColumn> columns = this.getColumns();
//        String tableComment = this.getComment();
//
//        // Вычисляем максимальную ширину
//        int maxNameLen = 0;
//        int maxTypeLen = 0;
//        int maxDefaultLen = 0;
//
//        for (Column col : columns) {
//            int nameLen = col.getCode().length();
//            int typeLen = col.isMandatory() ? col.getDatatype().length()
//                    : ("Nullable(" + col.getDatatype() + ")").length();
//
//            int defaultLen = 0;
//            if (col.getDefaultValue() != null && !col.getDefaultValue().isEmpty()) {
//                defaultLen = ("default " + col.getDefaultValue()).length();
//            }
//
//            maxNameLen = Math.max(maxNameLen, nameLen);
//            maxTypeLen = Math.max(maxTypeLen, typeLen);
//            maxDefaultLen = Math.max(maxDefaultLen, defaultLen);
//        }
//
//        sb.append("create table ")
//                .append(database).append('.').append(table)
//                .append(" on cluster main (\n");
//
//        for (int i = 0; i < columns.size(); i++) {
//            Column col = columns.get(i);
//
//            String name = col.getCode();
//            String type = col.isMandatory()
//                    ? col.getDatatype()
//                    : "Nullable(" + col.getDatatype() + ")";
//            String defaultVal = col.getDefaultValue();
//            String comment = col.getComment();
//
//            sb.append("    ")
//                    .append(String.format("%-" + maxNameLen + "s", name)).append("   ")
//                    .append(String.format("%-" + maxTypeLen + "s", type));
//
//            if (maxDefaultLen != 0) {
//                if (defaultVal != null && !defaultVal.isEmpty()) {
//                    sb.append("   ")
//                            .append(String.format("%-" + maxDefaultLen + "s", "default " + defaultVal));
//                } else {
//                    sb.append("   ")
//                            .append(String.format("%-" + maxDefaultLen + "s", ""));
//                }
//            }
//
//            if (comment != null && !comment.isEmpty()) {
//                String safeComment = comment.replace("'", "\\'");
//                sb.append("   comment '").append(safeComment).append('\'');
//            }
//
//            if (i < columns.size() - 1) {
//                sb.append(',');
//            }
//            sb.append('\n');
//        }
//
//        sb.append(")\n")
//                .append("engine = ReplicatedMergeTree\n")
//                .append("order by(ID таблицы)\n")
//                .append("partition by toYYYYMM(dm_dt)");
//
//        if (tableComment != null && !tableComment.isEmpty()) {
//            sb.append("\ncomment '").append(tableComment.replace("'", "\\'")).append("'");
//        }
//
//        sb.append(";\n\n");
//
//        // Роллбэк
//        sb.append("--rollback drop table ")
//                .append(database).append('.').append(table)
//                .append(" on cluster main sync;\n");
//
//        // Distributed-таблица
//        if (isWithDistributed()) {
//            sb.append("\ncreate table ")
//                    .append(database).append('.').append(table).append("_distributed on cluster main\n")
//                    .append("as ").append(database).append('.').append(table).append('\n')
//                    .append("engine = Distributed('main', '")
//                    .append(database).append("', '")
//                    .append(table).append("', rand());\n\n");
//
//            sb.append("--rollback drop table ")
//                    .append(database).append('.').append(table).append("_distributed on cluster main sync;\n");
//        }
//
//        return sb.toString();
//    }
//
//    public SQLFile createTable(CHTable table) {
//        String filename = TemplateBuilder.from(projectConfig.migrationNameConfig().fileNameTemplate())
//                .with("migrationNo", String.format(projectConfig.migrationNameConfig().migrationNoFormat(), table.getLastMigrationNo() + 1))
//                .with("actionName", "create-table")
//                .with("taskNo", taskConfig.taskNo())
//                .build();
//    }
//
//    public <C extends Column> List<SQLFile> addColumn(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
//    }
//
//    public <C extends Column> List<SQLFile> dropColumns(List<Schema<? extends Table<C>>> schemas, List<C> columns) {
//    }
//}
