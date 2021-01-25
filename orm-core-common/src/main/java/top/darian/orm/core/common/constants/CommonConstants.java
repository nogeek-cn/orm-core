package top.darian.orm.core.common.constants;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午6:54
 */


import java.util.regex.Pattern;

public interface CommonConstants {

    String TABLE_INFO = "TABLE_INFO";

    String TABLE_INFO_BEAN_NAME_PREFIX = "TableInfoServiceBean";

    String TABLE_INFO_BEAN_EARLY_INITIALIZATION = "orm.core.earlyInitialization";

    String TABLE_INFO_FIELD_NAMING_STRATEGY = "orm.core.fieldNamingStrategy";

    String TABLE_INFO_TABLE_NAMING_STRATEGY = "orm.core.tableNamingStrategy";

    String COMMA_SEPARATOR = ",";

    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
}

