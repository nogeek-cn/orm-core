package top.darian.orm.core.spring.util.module.mapping;


/**
 * SPI to determine how to name document fields in cases the field name is not manually defined.
 * <p>copy from <p/>
 *
 * @author Oliver Gierke
 * @see CamelCaseAbbreviatingFieldNamingStrategy
 * @see SnakeCaseFieldNamingStrategy
 * @since 1.9
 */
public interface FieldNamingStrategy {

    /**
     * Returns the field name to be used for the given
     *
     * @param fieldName must not be {@literal null} or empty;
     * @return
     */
    String fieldNameToColumnName(String fieldName);
}