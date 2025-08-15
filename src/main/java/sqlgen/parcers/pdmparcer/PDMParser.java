package sqlgen.parcers.pdmparcer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

import sqlgen.config.ProjectConfig;
import sqlgen.parcers.pdmparcer.model.PDMColumn;
import sqlgen.parcers.pdmparcer.model.PDMTable;

public class PDMParser {
    private String getTagValue(Element parent, String localName) {
        NodeList list = parent.getElementsByTagNameNS("*", localName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    public List<PDMTable> fillTables(ProjectConfig projectConfig, List<String> neededTables) {
        String inputPdm = Path.of(projectConfig.path(), projectConfig.modelPath()).toString();
        List<PDMTable> tables = new LinkedList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(inputPdm));

            NodeList tableNodes = doc.getElementsByTagNameNS("*", "Table");

            Map<String, Element> physicalDomains = new HashMap<>();
            Map<String, Element> shortcuts = new HashMap<>();

            // Сохраняем все PhysicalDomain по Id
            NodeList domainNodes = doc.getElementsByTagNameNS("*", "PhysicalDomain");
            for (int i = 0; i < domainNodes.getLength(); i++) {
                Element dom = (Element) domainNodes.item(i);
                String id = dom.getAttribute("Id");
                physicalDomains.put(id, dom);
            }

            // Сохраняем все Shortcut по Id
            NodeList shortcutNodes = doc.getElementsByTagNameNS("*", "Shortcut");
            for (int i = 0; i < shortcutNodes.getLength(); i++) {
                Element sc = (Element) shortcutNodes.item(i);
                String id = sc.getAttribute("Id");
                shortcuts.put(id, sc);
            }

            for (int i = 0; i < tableNodes.getLength(); i++) {
                Element pdmTable = (Element) tableNodes.item(i);

                String tableCode = getTagValue(pdmTable, "Code");
                if (!neededTables.contains(tableCode)) continue;
                String tableComment = getTagValue(pdmTable, "Comment");
                PDMTable table = new PDMTable(
                        tableCode,
                        tableComment
                );

                Element columnsContainer = (Element) pdmTable.getElementsByTagNameNS("*", "Columns").item(0);
                NodeList pdmColumns = columnsContainer.getElementsByTagNameNS("*", "Column");
                List<PDMColumn> columns = new LinkedList<>();

                for (int j = 0; j < pdmColumns.getLength(); j++) {
                    Element pdmColumn = (Element) pdmColumns.item(j);

                    String colName = getTagValue(pdmColumn, "Code");
                    String type = getTagValue(pdmColumn, "DataType");

                    String domainCode = null;
                    Element domainEl = (Element) pdmColumn.getElementsByTagNameNS("*", "Domain").item(0);
                    if (domainEl != null) {
                        Element physRef = (Element) domainEl.getElementsByTagNameNS("*", "PhysicalDomain").item(0);
                        Element shortRef = (Element) domainEl.getElementsByTagNameNS("*", "Shortcut").item(0);

                        if (physRef != null) {
                            String refId = physRef.getAttribute("Ref");
                            Element dom = physicalDomains.get(refId);
                            if (dom != null) {
                                domainCode = getTagValue(dom, "Name");
                            }
                        } else if (shortRef != null) {
                            String refId = shortRef.getAttribute("Ref");
                            Element shortcut = shortcuts.get(refId);
                            if (shortcut != null) {
                                domainCode = getTagValue(shortcut, "Name");
                            }
                        }
                    }

                    String comment = getTagValue(pdmColumn, "Name");
                    String defaultValue = getTagValue(pdmColumn, "Default");

                    columns.add(new PDMColumn(
                            colName,
                            type,
                            defaultValue,
                            !comment.isEmpty()? comment : null,
                            !defaultValue.isEmpty(),
                            domainCode
                    ));
                }

                table.setColumns(columns);
                tables.add(table);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tables;
    }
}