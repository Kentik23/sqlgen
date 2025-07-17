package sqlgen;

import sqlgen.core.GeneratorConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.io.SaveFileException;
import sqlgen.core.model.Column;
import sqlgen.core.model.Table;
import sqlgen.generators.clickhouse.model.CHColumn;
import sqlgen.generators.clickhouse.model.CHSchema;
import sqlgen.generators.clickhouse.model.CHTable;
import sqlgen.generators.greenplum.model.GPColumn;
import sqlgen.generators.greenplum.model.GPSchema;
import sqlgen.generators.greenplum.model.GPTable;

import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorApplication {
    public static void run() {
        List<Table> tables = new ArrayList<>();

        tables.add(new GPTable(
                "dm_labm_notification",
                "ПК по Уведомлениям. Трудовая миграция"
        ));

        tables.add(new GPTable(
                "dm_notification_actual",
                "ПК по действительным уведомлениям. Трудовая миграция"
        ));

        tables.add(new GPTable(
                "dm_notification_actual_retrospective",
                "ПК по действительным уведомлениям. Трудовая миграция. Ретроспектива"
        ));

        List<Column> columns = new ArrayList<>();

        columns.add(new GPColumn(
                "leave_dt",
                "date",
                null,
                "Дата последнего выезда из РФ",
                false
        ));

        columns.add(new GPColumn(
                "account_dt",
                "date",
                null,
                "Дата последней постановки на МУ по МП",
                false
        ));

        List<SQLFile> sqlFiles;
        SQLGenerator sqlGenerator = new SQLGenerator(new GeneratorConfig(
                "src/main/gp/databases/dwh/schemas",
                "aleandivanov",
                "GISMUBI-28081",
                "src/main/gp/databases/dwh/_changelogs"
        ));

        sqlFiles = sqlGenerator.addColumns(List.of(new GPSchema("dm_click", tables)), columns);
        sqlGenerator.addChangelogFiles(sqlFiles);

        List<Table> clickTables = new ArrayList<>();

        clickTables.add(new CHTable(
                "dm_labm_notification",
                "ПК по Уведомлениям. Трудовая миграция"
        ));

        clickTables.add(new CHTable(
                "dm_notification_actual",
                "ПК по действительным уведомлениям. Трудовая миграция"
        ));

        clickTables.add(new CHTable(
                "dm_notification_actual_retrospective",
                "ПК по действительным уведомлениям. Трудовая миграция. Ретроспектива"
        ));

        clickTables.add(new CHTable(
                "dm_labm_notification_checklist",
                "ПК по Уведомлениям. Трудовая миграция с КС"
        ));

        clickTables.add(new CHTable(
                "dm_labm_notification_stg",
                "ПК по Уведомлениям. Трудовая миграция"
        ));

        clickTables.add(new CHTable(
                "dm_notification_actual_stg",
                "ПК по действительным уведомлениям. Трудовая миграция"
        ));

        clickTables.add(new CHTable(
                "dm_notification_actual_retrospective_stg",
                "ПК по действительным уведомлениям. Трудовая миграция. Ретроспектива"
        ));

        clickTables.add(new CHTable(
                "dm_labm_notification_checklist_stg",
                "ПК по Уведомлениям. Трудовая миграция с КС"
        ));

        List<Column> clickColumns = new ArrayList<>();

        clickColumns.add(new CHColumn(
                "leave_dt",
                "Date",
                null,
                "Дата последнего выезда из РФ",
                false
        ));

        clickColumns.add(new CHColumn(
                "account_dt",
                "Date",
                null,
                "Дата последней постановки на МУ по МП",
                false
        ));

        SQLGenerator csqlGenerator = new SQLGenerator(new GeneratorConfig(
                "src/main/ch/databases/default/schemas",
                "aleandivanov",
                "GISMUBI-28081",
                "src/main/ch/databases/default/_changelogs"
        ));

        List<SQLFile> csqlFiles;
        csqlFiles = csqlGenerator.addColumns(List.of(new CHSchema("dm_public", clickTables)), clickColumns);

        csqlGenerator.addChangelogFiles(csqlFiles);

        SQLWriter sqlWriter = new SQLWriter();
        try {
            //sqlWriter.saveAs(sqlFiles, "C:/Users/aleandivanov/ETL/dwh");
            sqlWriter.saveAs(csqlFiles, "C:/Users/aleandivanov/ETL/dwh");
        } catch (SaveFileException e) {
            System.out.println("Невозмоно сохранить файл");
            e.printStackTrace(System.err);
        }
    }
}
