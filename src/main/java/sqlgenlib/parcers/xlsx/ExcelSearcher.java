//package sqlgenlib.parcers.xlsx;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//public class ExcelSearcher {
//
//    private final String filePath;
//
//    public ExcelSearcher(String filePath) {
//        this.filePath = filePath;
//    }
//
//    /**
//     * Ищет строку по значению в заданной колонке и возвращает значение другой колонки.
//     *
//     * @param searchColumnIndex индекс колонки, где ищем (0 = A, 1 = B, ...)
//     * @param returnColumnIndex индекс колонки, откуда берем результат
//     * @param searchValue значение для поиска
//     * @return найденное значение или null, если ничего не найдено
//     */
//    public String findValue(int searchColumnIndex, int returnColumnIndex, String searchValue) {
//        try (FileInputStream fis = new FileInputStream(filePath);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(0); // берем первый лист
//            for (Row row : sheet) {
//                Cell cell = row.getCell(searchColumnIndex);
//                if (cell != null && getCellValue(cell).equals(searchValue)) {
//                    Cell returnCell = row.getCell(returnColumnIndex);
//                    return returnCell != null ? getCellValue(returnCell) : null;
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка чтения Excel файла: " + e.getMessage(), e);
//        }
//        return null;
//    }
//
//    private String getCellValue(Cell cell) {
//        return switch (cell.getCellType()) {
//            case STRING -> cell.getStringCellValue();
//            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
//            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
//            case FORMULA -> cell.getCellFormula();
//            case BLANK, _NONE, ERROR -> "";
//        };
//    }
//
//    public static void main(String[] args) {
//        ExcelSearcher searcher = new ExcelSearcher("example.xlsx");
//
//        // Пример: ищем строку, где в колонке A ("0") стоит "123", возвращаем колонку C ("2")
//        String result = searcher.findValue(0, 2, "123");
//
//        System.out.println("Результат: " + result);
//    }
//}
