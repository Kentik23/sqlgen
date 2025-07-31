package sqlgen.examples;

import sqlgen.config.AppConfig;
import sqlgen.core.SQLGenerator;
import sqlgen.core.io.SQLFile;
import sqlgen.core.io.SQLWriter;
import sqlgen.core.model.Schema;
import sqlgen.core.model.Table;
import sqlgen.generators.clickhouse.CHSQLGenerator;
import sqlgen.generators.clickhouse.model.CHColumn;
import sqlgen.generators.clickhouse.model.CHDictionary;
import sqlgen.generators.clickhouse.model.CHSchema;
import sqlgen.parcers.ChangeLogParser;

import java.util.ArrayList;
import java.util.List;

public class CreateDictionaries {
    public static List<Table> loadDictionariesFromData() {
        List<Table> dictionaries = new ArrayList<>();
        Schema schema = new CHSchema("dictionaries", dictionaries);

        dictionaries.add(new CHDictionary(schema, "dim_perehvat_sprichina_del_event", null,
                List.of(new CHColumn("prich_text", "String", null, "", false)),
                0,
                new CHColumn("prich_kod", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sprichina_del_event"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_perehvat_szona_list", null,
                List.of(new CHColumn("zona_name", "String", null, "", false)),
                0,
                new CHColumn("zona_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_szona_list_v"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_perehvat_szona", null,
                List.of(new CHColumn("zona_name", "String", null, "", false)),
                0,
                new CHColumn("zona_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_szona"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_perehvat_szona_list_camer", null,
                List.of(new CHColumn("camera", "String", null, "", false)),
                0,
                new CHColumn("zona_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_szona_list_camer"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sreg_type", null,
                List.of(new CHColumn("name", "String", null, "", false)),
                0,
                new CHColumn("id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sreg_type"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_region", null,
                List.of(new CHColumn("region_name", "String", null, "", false)),
                0,
                new CHColumn("resp_kod", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_adr_region_v"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sgibdd", null,
                List.of(new CHColumn("resp_kod", "String", null, "", false)),
                0,
                new CHColumn("id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sgibdd"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_stotv", null,
                List.of(new CHColumn("stotv_kod", "String", null, "", false)),
                0,
                new CHColumn("stotv_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_stotv"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sdecis", null,
                List.of(new CHColumn("decis_name", "String", null, "", false)),
                0,
                new CHColumn("decis_kod", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sdecis"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sstad_delo", null,
                List.of(new CHColumn("stad_delo_name", "String", null, "", false)),
                0,
                new CHColumn("stad_delo_kod", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sstad_delo"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sstad_ispoln", null,
                List.of(new CHColumn("stad_ispoln_name", "String", null, "", false)),
                0,
                new CHColumn("stad_ispoln_kod", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sstad_ispoln"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sfssp_doc_types", null,
                List.of(new CHColumn("name", "String", null, "", false)),
                0,
                new CHColumn("id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sfssp_doc_types"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sisp", null,
                List.of(new CHColumn("isp_name", "String", null, "", false)),
                0,
                new CHColumn("isp_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sisp"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sinsp", null,
                List.of(new CHColumn("insp_name", "String", null, "", false)),
                0,
                new CHColumn("insp_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sinsp"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sworkflow_stages", null,
                List.of(new CHColumn("ws_name", "String", null, "", false)),
                0,
                new CHColumn("ws_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sworkflow_stages"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sbanks", null,
                List.of(new CHColumn("bankname", "String", null, "", false)),
                0,
                new CHColumn("id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sbanks"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sdocs_tip", null,
                List.of(new CHColumn("doc_tip_name", "String", null, "", false)),
                0,
                new CHColumn("doc_tip", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sdocs_tip"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_spnpa", null,
                List.of(
                        new CHColumn("pnpa_kod", "String", null, "", false),
                        new CHColumn("pnpa_name", "String", null, "", false)
                ),
                0,
                new CHColumn("pnpa_id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_spnpa"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sdocs_vid", null,
                List.of(new CHColumn("doc_vid_name", "String", null, "", false)),
                0,
                new CHColumn("doc_vid", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sdocs_vid"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_sappeal_types", null,
                List.of(new CHColumn("name", "String", null, "", false)),
                0,
                new CHColumn("id", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_sappeal_types"
        ));

        dictionaries.add(new CHDictionary(schema, "dim_apr_suchast_tip", null,
                List.of(new CHColumn("uchast_tip_name", "String", null, "", false)),
                0,
                new CHColumn("uchast_tip", "Int64", null, "", true),
                "ods_gibdd_traffic_m", "s_suchast_tip"
        ));

        return dictionaries;
    }

    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();

            List<Table> tables = new ArrayList<>();

            tables.addAll(loadDictionariesFromData());

            List<SQLFile> sqlFiles;
            CHSQLGenerator sqlGenerator = new CHSQLGenerator(
                appConfig.getProjectConfig().chConfig(),
                appConfig.getTaskConfig()
            );

            sqlFiles = sqlGenerator.createDictionaries(List.of(new CHSchema("dictionaries", tables)));

            sqlGenerator.addChangelogFiles(sqlFiles, "create-dictionary", ChangeLogParser.getMasterChangeLog(appConfig, appConfig.getProjectConfig().chConfig()));

            SQLWriter sqlWriter = new SQLWriter();
            sqlWriter.saveAs(sqlFiles, appConfig.getProjectConfig().path());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
