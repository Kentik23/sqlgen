package sqlgenlib.examples;

import sqlgenlib.AppConfig;
import sqlgenlib.config.DBConfig;
import sqlgenlib.core.SQLGenerator;
import sqlgenlib.core.io.SQLFile;
import sqlgenlib.core.io.SQLWriter;
import sqlgenlib.modules.clickhouse.model.CHColumn;
import sqlgenlib.modules.clickhouse.model.Dictionary;
import sqlgenlib.parcers.ChangelogParser;
import sqlgenlib.parcers.DWHProjectParser;
import sqlgenlib.parcers.MigrationFilesSearcher;
import sqlgenlib.parcers.TableMigrationParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class myTableMigrationParser extends TableMigrationParser<Dictionary> {
    @Override
    public Dictionary parseTable(List<SQLFile> sqlFiles) {
        // сортируем по имени файла (чтобы 1-0-0 шла раньше 2-0-0)
        sqlFiles.sort(Comparator.comparing(SQLFile::getRelativePath));

        Dictionary current = null;

        for (SQLFile file : sqlFiles) {
            String content = file.getContent().toLowerCase(Locale.ROOT);

            if (content.contains("create or replace dictionary")) {
                current = parseCreate(file.getContent(), file.getRelativePath());
            }
            else if (content.contains("drop dictionary")) {
                // по-хорошему надо обнулить, но чаще drop идёт перед create
                current = null;
            }
            else if (content.contains("reinit-dictionary")) {
                // для reinit просто парсим новый create, как будто заново
                current = parseCreate(file.getContent(), file.getRelativePath());
            }
            // TODO: сюда можно добавить alter / drop column / add column
        }

        return current;
    }
    private Dictionary parseCreate(String sql, String fileName) {
        // schema.dictName
        Pattern dictPattern = Pattern.compile("create or replace dictionary\\s+([a-z0-9_]+)\\.([a-z0-9_]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = dictPattern.matcher(sql);
        String schema = null, dictName = null;
        if (m.find()) {
            schema = m.group(1);
            dictName = m.group(2);
        }

        // Вырезаем блок с колонками
        Pattern colsBlockPattern = Pattern.compile(
                "\\((.*?)\\)\\s*primary key",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher blockMatcher = colsBlockPattern.matcher(sql);

        List<CHColumn> columns = new ArrayList<>();
        if (blockMatcher.find()) {
            String columnsSQL = blockMatcher.group(1).trim();

            // Теперь парсим сами колонки
            // Учитываем комментарий: colName Type [comment '...']
            Pattern colPattern = Pattern.compile(
                    "([a-z0-9_]+)\\s+([a-z0-9()]+(?:,[a-z0-9()]+)*)(?:\\s+comment\\s+'([^']*)')?",
                    Pattern.CASE_INSENSITIVE
            );
            Matcher cm = colPattern.matcher(columnsSQL);

            while (cm.find()) {
                String colName = cm.group(1);
                String colType = cm.group(2);
                String colComment = cm.group(3);

                columns.add(new CHColumn(
                        colName,
                        colType,
                        null,   // здесь нормальный коммент
                        colComment,         // placeholder под автора/источник
                        false,
                        false
                ));
            }
        }


        // primary key
        String pk = null;
        Matcher pkM = Pattern.compile("primary key\\s+([a-z0-9_]+)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (pkM.find()) {
            pk = pkM.group(1);
        }

        // source DB + table
        String sourceDb = null, sourceTable = null;
        Matcher srcM = Pattern.compile("db\\s+'([^']+)'\\s+table\\s+'([^']+)'", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (srcM.find()) {
            sourceDb = srcM.group(1);
            sourceTable = srcM.group(2);
        }

        // migration number (можно вытаскивать из имени файла: 1-0-0 …)
        int migrationNo = extractMigrationNo(fileName);

        // собрать словарь
        Dictionary dict = new Dictionary(
                schema,
                dictName,
                null, // comment пока пусто
                columns,
                migrationNo,
                sourceDb,
                sourceTable
        );

        if (pk != null) {
            for (CHColumn c : columns) {
                if (c.getCode().equalsIgnoreCase(pk)) {
                    c.setPrimary(true);
                }
            }
        }

        return dict;
    }

    private int extractMigrationNo(String fileName) {
        // примитив: берём первые числа "1-0-0" → 100, "2-0-0" → 200 и т.п.
        Matcher m = Pattern.compile("(\\d+)-(\\d+)-(\\d+)").matcher(fileName);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
}

public class ReinitDictionaries {
    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig();
            DBConfig dbConfig = appConfig.getProjectConfig().databases().stream().filter(x -> x.database().equals("default")).findFirst().get();

            List<String> dictianariesNames = List.of(
                "dim_1ap_stotv",
                "dim_apr_fssp_sexecutive_status",
                "dim_apr_mc_high_court_decision_reason",
                "dim_apr_post_sending_contract",
                "dim_apr_region",
                "dim_apr_sappeal_categories",
                "dim_apr_sappeal_types",
                "dim_apr_sbanks",
                "dim_apr_scard_category",
                "dim_apr_sdecis",
                "dim_apr_sdecis_status",
                "dim_apr_sdelo_vid",
                "dim_apr_sdoc_form",
                "dim_apr_sdoc_state",
                "dim_apr_sdocs_tip",
                "dim_apr_sdocs_vid",
                "dim_apr_sfine_nonpayment_reason",
                "dim_apr_sfssp_doc_types",
                "dim_apr_sisp",
                "dim_apr_snotif_type",
                "dim_apr_spnpa",
                "dim_apr_sreg_type",
                "dim_apr_sstad_delo",
                "dim_apr_sstad_ispoln",
                "dim_apr_suchast_status",
                "dim_apr_suchast_tip",
                "dim_apr_suchast_vid",
                "dim_apr_svehs_vid",
                "dim_apr_svf_sviol",
                "dim_apr_sworkflow_stages",
                "dim_camera_zone_operator",
                "dim_kpi",
                "dim_perehvat_sisp_isp",
                "dim_perehvat_sisp_otdel",
                "dim_perehvat_sprichina_del_event",
                "dim_perehvat_szona",
                "dim_perehvat_szona_list_camer",
                "dim_spr_country",
                "dim_spr_kadr_region",
                "dim_tr_instances",
                "dim_traffic_camera",
                "dim_traffic_camera_class",
                "dim_traffic_country",
                "dim_traffic_direction",
                "dim_traffic_pod_country",
                "dim_traffic_pod_regno_color",
                "dim_traffic_pod_vehicle_body_type",
                "dim_traffic_pod_vehicle_brand",
                "dim_traffic_pod_vehicle_color",
                "dim_traffic_pod_vehicle_model",
                "dim_traffic_queries_svf_refuse_reason",
                "dim_traffic_sgibdd_camera",
                "dim_traffic_sgibdd_viol",
                "dim_traffic_sisp",
                "dim_traffic_sviol",
                "dim_v_sdtp_ed_level",
                "dim_v_sdtp_family_status",
                "dim_v_sdtp_uchast_ranks"
            );

            SQLGenerator sqlGenerator = new SQLGenerator(appConfig.getProjectConfig(), dbConfig, appConfig.getTaskConfig());

            List<SQLFile> sqlFiles = new ArrayList<>();
            for (String dictName : dictianariesNames) {
                Dictionary dict = DWHProjectParser.getTable(
                        new MigrationFilesSearcher(appConfig.getProjectConfig(), dbConfig) {
                            @Override
                            public String getPathTemplate() {
                                return super.getPathTemplate().replace("/tables","");
                            }
                        },
                        new myTableMigrationParser(),
                        "dictionaries",
                        dictName
                );

                if (dict == null) {
                    System.out.println("Э баля " + dictName);
                }

                sqlFiles.add(sqlGenerator.reinitDictionary(dict));
            }

            sqlGenerator.addChangelogFiles(sqlFiles, "reinit-dictionaries", ChangelogParser.getMasterChangeLog(appConfig.getProjectConfig(), dbConfig));
            SQLWriter.saveAs(sqlFiles, Path.of(appConfig.getProjectConfig().path(), appConfig.getProjectConfig().dwhPath()).toString());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}
