package sqlgen.entity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sqlgen.entity.pdmsource.Column;
import sqlgen.entity.pdmsource.Table;

public class PDMParser {

    public static void main(String[] args) {
        String pdmFilePath = "C:\\Users\\alyusmirnov\\Desktop\\bi_cpt.pdm";
        try {
            List<Table> tables = parsePdmFile(pdmFilePath);
            for(Table table : tables){;
                if (table.getColumns().isEmpty()){
                    continue;
                }
                System.out.println(table.toString());
                getColumnCodesByTable(table).forEach(System.out::println);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Table> parsePdmFile(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        List<Table> tables = new ArrayList<>();
        NodeList tableNodes = document.getElementsByTagName("o:Table");

        for (int i = 0; i < tableNodes.getLength(); i++) {
            Element tableElement = (Element) tableNodes.item(i);
            Table table = parseTable(tableElement);
            tables.add(table);
        }

        return tables;
    }

    private static Table parseTable(Element tableElement) {
        Table table = new Table();
        // Извлекаем информацию о таблице
        table.setId(tableElement.getAttribute("Id"));
        table.setName(getElementText(tableElement, "a:Name"));
        table.setCode(getElementText(tableElement, "a:Code"));
        table.setComment(getElementText(tableElement, "a:Comment"));

        // Парсим колонки
        NodeList columnNodes = tableElement.getElementsByTagName("o:Column");
        for (int i = 0; i < columnNodes.getLength(); i++) {
            Element columnElement = (Element) columnNodes.item(i);
            Column column = parseColumn(columnElement);
            table.addColumn(column);
        }

        return table;
    }

    private static Column parseColumn(Element columnElement) {
        Column column = new Column();

        column.setId(columnElement.getAttribute("Id"));
        column.setName(getElementText(columnElement, "a:Name"));
        column.setCode(getElementText(columnElement, "a:Code"));
        column.setDataType(getElementText(columnElement, "a:DataType"));
        column.setLength(getElementText(columnElement, "a:Length"));
        column.setPrecision(getElementText(columnElement, "a:Precision"));
        column.setMandatory(getElementText(columnElement, "a:Mandatory"));
        column.setComment(getElementText(columnElement, "a:Comment"));

        // Парсим первичный ключ
        NodeList keyNodes = columnElement.getElementsByTagName("c:Key");
        if (keyNodes.getLength() > 0) {
            Element keyElement = (Element) keyNodes.item(0);
            column.setPrimaryKey("true".equals(keyElement.getAttribute("IsPrimary")));
        }
        column.toString();

        return column;
    }

    private static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public static List<String> getColumnCodesByTable(Table table){
        List<String> columns = new ArrayList<String>();
        columns.addAll(
                table.getColumns()
                        .stream()
                        .map(Column::getCode)
                        .collect(Collectors.toList()));
        return columns;
    }


}