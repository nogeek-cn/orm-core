package top.darian.orm.core.spring.util;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  上午2:19
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Utility methods for {@link String} parsing.
 *
 * @author Oliver Gierke
 * @since 1.5
 */
public class ParsingUtils {

    private static final String UPPER = "\\p{Lu}|\\P{InBASIC_LATIN}";
    private static final String LOWER = "\\p{Ll}";
    private static final String CAMEL_CASE_REGEX = "(?<!(^|[%u_$]))(?=[%u])|(?<!^)(?=[%u][%l])". //
            replace("%u", UPPER).replace("%l", LOWER);

    private static final Pattern CAMEL_CASE = Pattern.compile(CAMEL_CASE_REGEX);

    /**
     * Splits up the given camel-case {@link String}.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    public static List<String> splitCamelCase(String source) {
        return split(source, false);
    }

    /**
     * Splits up the given camel-case {@link String} and returns the parts in lower case.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    public static List<String> splitCamelCaseToLower(String source) {
        return split(source, true);
    }

    /**
     * Reconcatenates the given camel-case source {@link String} using the given delimiter. Will split up the camel-case
     * {@link String} and use an uncapitalized version of the parts.
     *
     * @param source    must not be {@literal null}.
     * @param delimiter must not be {@literal null}.
     * @return
     */
    public static String reconcatenateCamelCase(String source, String delimiter) {

        Assert.notNull(source, "Source string must not be null!");
        Assert.notNull(delimiter, "Delimiter must not be null!");

        return StringUtils.collectionToDelimitedString(splitCamelCaseToLower(source), delimiter);
    }

    private static List<String> split(String source, boolean toLower) {

        Assert.notNull(source, "Source string must not be null!");

        String[] parts = CAMEL_CASE.split(source);
        List<String> result = new ArrayList<>(parts.length);

        for (String part : parts) {
            result.add(toLower ? part.toLowerCase() : part);
        }

        return Collections.unmodifiableList(result);
    }
}

