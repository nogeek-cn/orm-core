package top.darian.orm.core.spring.util.module.mapping;

/**
 * {@link FieldNamingStrategy} that abbreviates field names by using the very first letter of the camel case parts of
 * the fileldName's name.
 *
 * @author Oliver Gierke
 * @since 1.9
 */
public class CamelCaseAbbreviatingFieldNamingStrategy extends CamelCaseSplittingFieldNamingStrategy {

    /**
     * Creates a new {@link CamelCaseAbbreviatingFieldNamingStrategy}.
     */
    public CamelCaseAbbreviatingFieldNamingStrategy() {
        super("");
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.core.mapping.CamelCaseSplittingFieldNamingStrategy#preparePart(java.lang.String)
     */
    @Override
    protected String preparePart(String part) {
        return part.substring(0, 1);
    }
}
