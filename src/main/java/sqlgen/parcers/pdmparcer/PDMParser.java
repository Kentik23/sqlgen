package sqlgen.parcers.pdmparcer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sqlgen.parcers.Parcer;
import sqlgen.parcers.pdmparcer.pdmsource.PDMColumn;
import sqlgen.parcers.pdmparcer.pdmsource.PDMTable;


public class PDMParser implements Parcer {

    private List<PDMTable> tables = new ArrayList<>();

    public List<PDMTable> getTableBases() {
        return tables;
    }

    public void main(String[] args) {
        String pdmFilePath = "C:\\Users\\alyusmirnov\\Desktop\\bi_cpt.pdm";
        try {
            tables = parseFile(pdmFilePath);
            for(PDMTable table : tables){;
                if (table.getColumns().isEmpty()){
                    continue;
                }
                System.out.println(table.toString());
                getColumnsCodeByColumns(tables.getFirst().getColumns()).forEach(System.out::println);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<PDMTable> parseFile(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        List<PDMTable> tables = new ArrayList<>();
        NodeList tableNodes = document.getElementsByTagName("o:Table");

        for (int i = 0; i < tableNodes.getLength(); i++) {
            Element tableElement = (Element) tableNodes.item(i);
            PDMTable table = parseTable(tableElement);
            if(table.getColumns().isEmpty()){
                continue;
            }
            tables.add(table);
        }
        this.tables = tables;

        return tables;
    }

    private PDMTable parseTable(Element tableElement) {
        PDMTable table = new PDMTable();
        // Извлекаем информацию о таблице
        table.setCode(getElementText(tableElement, "a:Code"));
        // Парсим колонки
        NodeList columnNodes = tableElement.getElementsByTagName("o:Column");
        for (int i = 0; i < columnNodes.getLength(); i++) {
            Element columnElement = (Element) columnNodes.item(i);
            PDMColumn column = parseColumn(columnElement);
            if (column.getCode() == null){
                continue;
            }
            table.addColumn(column);
        }

        return table;
    }

    private PDMColumn parseColumn(Element columnElement) {
        PDMColumn column = new PDMColumn();
        column.setCode(getElementText(columnElement, "a:Code"));
        column.setDataType(getElementText(columnElement, "a:DataType"));
        column.setLength(getElementText(columnElement, "a:Length"));
        column.setPrecision(getElementText(columnElement, "a:Precision"));
        column.setMandatory(getElementText(columnElement, "a:Mandatory"));
        column.setComment(getElementText(columnElement, "a:Comment"));

        return column;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public List<PDMColumn> getColumnByTableCode(String tableCode){
        for (PDMTable table: tables){
            if(table.getCode().equals(tableCode)){
                return table.getColumns();
            }
        }
        return null;
    }

    public List<String> getColumnsCodeByColumns(List<PDMColumn> columns){
        List<String> columnsCode = new ArrayList<>();
        for (PDMColumn column: columns){
            columnsCode.add(column.getCode());
        }
        return columnsCode;
    }

    public PDMTable getTableByCode(String tableCode){
        for (PDMTable table: tables){
            if(table.getCode().equals(tableCode)){
                return table;
            }
        }
        return null;
    }



}