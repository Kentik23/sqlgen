//package sqlgenlib.generators.greenplum.model;
//
//import sqlgenlib.core.model.Column;
//
//public class GPColumn extends Column {
//    public GPColumn(String code, String datatype, String defaultValue, String comment, boolean mandatory, boolean primary) {
//        super(code, datatype, defaultValue, comment, mandatory, primary);
//    }
//    public GPColumn(Column column) {
//        super(column);
//    }
//
//    @Override
//    public GPColumn copy() {
//        return new GPColumn(
//                getCode(),
//                getDatatype(),
//                getDefaultValue(),
//                getComment(),
//                isMandatory(),
//                isPrimary()
//        );
//    }
//}
