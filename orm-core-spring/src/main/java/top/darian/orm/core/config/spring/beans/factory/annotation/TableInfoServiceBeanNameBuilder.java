package top.darian.orm.core.config.spring.beans.factory.annotation;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.StringUtils;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午4:15
 */

import static com.alibaba.spring.util.AnnotationUtils.getAttribute;

/**
 * TableInfo {@link top.darian.orm.core.config.annotation.TableInfo} Bean Builder
 *
 * @since 2.6.5
 */
public class TableInfoServiceBeanNameBuilder {

    private static final String SEPARATOR = ":";

    private static final String TABLE_INFO_BEAN_NAME_PREFIX = "TableInfoServiceBean";
    // Required
    private final String interfaceClassName;

    private TableInfoServiceBeanNameBuilder(Class<?> interfaceClass) {
        this(interfaceClass.getName());
    }

    private TableInfoServiceBeanNameBuilder(String interfaceClassName) {
        this.interfaceClassName = interfaceClassName;
    }

    private TableInfoServiceBeanNameBuilder(AnnotationAttributes attributes, Class<?> defaultInterfaceClass) {
        this(defaultInterfaceClass.getName());
    }

    /**
     * @param attributes
     * @param defaultInterfaceClass
     * @return
     * @since 2.7.3
     */
    public static TableInfoServiceBeanNameBuilder create(AnnotationAttributes attributes,
                                                         Class<?> defaultInterfaceClass) {
        return new TableInfoServiceBeanNameBuilder(attributes, defaultInterfaceClass);
    }

    public static TableInfoServiceBeanNameBuilder create(Class<?> interfaceClass) {
        return new TableInfoServiceBeanNameBuilder(interfaceClass);
    }

    private static void append(StringBuilder builder, String value) {
        if (StringUtils.hasText(value)) {
            builder.append(SEPARATOR).append(value);
        }
    }


    public String build() {
        StringBuilder beanNameBuilder = new StringBuilder(TABLE_INFO_BEAN_NAME_PREFIX);
        // Required
        append(beanNameBuilder, interfaceClassName);
        // Optional
        // Build and remove last ":"
        return beanNameBuilder.toString();
    }
}
