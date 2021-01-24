
package top.darian.orm.core.spring.util.module.mapping;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.darian.orm.core.spring.util.ParsingUtils;

/**
 * Configurable {@link FieldNamingStrategy} that splits up camel-case property names and reconcatenates them using a
 * configured delimiter. Individual parts of the name can be manipulated using {@link #preparePart(String)}.
 *
 * @author Oliver Gierke
 * @since 1.9
 */
public class CamelCaseSplittingFieldNamingStrategy implements FieldNamingStrategy {

    private final String delimiter;

    /**
     * Creates a new {@link CamelCaseSplittingFieldNamingStrategy}.
     *
     * @param delimiter must not be {@literal null}.
     */
    public CamelCaseSplittingFieldNamingStrategy(String delimiter) {

        Assert.notNull(delimiter, "Delimiter must not be null!");
        this.delimiter = delimiter;
    }

    /**
     * (non-Javadoc)
     *
     * @see FieldNamingStrategy#fieldNameToColumnName(String) (fieldName)
     */
    @Override
    public String fieldNameToColumnName(String fieldName) {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(fieldName);
        List<String> result = new ArrayList<>();

        for (String part : parts) {
            String candidate = preparePart(part);
            if (StringUtils.hasText(candidate)) {
                result.add(candidate);
            }
        }

        return StringUtils.collectionToDelimitedString(result, delimiter);
    }

    /**
     * Callback to prepare the uncapitalized part obtained from the split up of the camel case source. Default
     * implementation returns the part as is.
     *
     * @param part
     * @return
     */
    protected String preparePart(String part) {
        return part;
    }
}
