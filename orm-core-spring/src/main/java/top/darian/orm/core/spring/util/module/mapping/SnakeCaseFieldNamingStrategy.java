package top.darian.orm.core.spring.util.module.mapping;

/**
 * {@link FieldNamingStrategy} that translates typical camel case Java property names to lower case JSON element names,
 * separated by underscores.
 *
 * <p><p/>
 *
 * @author Ryan Tenney
 * @author Oliver Gierke
 * @since 1.5
 */
public class SnakeCaseFieldNamingStrategy extends CamelCaseSplittingFieldNamingStrategy {

    /**
     * Creates a new {@link SnakeCaseFieldNamingStrategy}.
     */
    public SnakeCaseFieldNamingStrategy() {
        super("_");
    }
}
