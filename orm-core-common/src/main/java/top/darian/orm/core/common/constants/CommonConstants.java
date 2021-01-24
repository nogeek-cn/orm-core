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

    String ANY_VALUE = "*";

    char COMMA_SEPARATOR_CHAR = ',';

    String COMMA_SEPARATOR = ",";

    String DOT_SEPARATOR = ".";

    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    String PATH_SEPARATOR = "/";

    String PROTOCOL_SEPARATOR = "://";


    String REGISTRY_SEPARATOR = "|";

    Pattern REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|;]+\\s*");

    Pattern D_REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|]+\\s*");

    String SEMICOLON_SEPARATOR = ";";

    Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");

    Pattern EQUAL_SPLIT_PATTERN = Pattern.compile("\\s*[=]+\\s*");

    Pattern COLON_SPLIT_PATTERN = Pattern.compile("\\s*[:]+\\s*");

    String REMOVE_VALUE_PREFIX = "-";

    String PROPERTIES_CHAR_SEPARATOR = "-";

    String UNDERLINE_SEPARATOR = "_";

    String SEPARATOR_REGEX = "_|-";

    String GROUP_CHAR_SEPARATOR = ":";

    String HIDE_KEY_PREFIX = ".";

    String DOT_REGEX = "\\.";


}

