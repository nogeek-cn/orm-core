package top.darian.orm.core.spring.beans.factory.annotation;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午8:59
 */

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.PropertyResolver;

import java.lang.annotation.Annotation;
import java.util.Map;

import static com.alibaba.spring.util.AnnotationUtils.getAttributes;

/**
 * {@link Annotation} {@link PropertyValues} Adapter
 *
 * @see Annotation
 * @see PropertyValues
 * @since 2.5.11
 */
class AnnotationPropertyValuesAdapter implements PropertyValues {

    private final PropertyValues delegate;

    /**
     * @param attributes
     * @param propertyResolver
     * @param ignoreAttributeNames
     * @since 2.7.3
     */
    public AnnotationPropertyValuesAdapter(Map<String, Object> attributes, PropertyResolver propertyResolver,
                                           String... ignoreAttributeNames) {
        this.delegate = new MutablePropertyValues(getAttributes(attributes, propertyResolver, ignoreAttributeNames));
    }

    public AnnotationPropertyValuesAdapter(Annotation annotation, PropertyResolver propertyResolver,
                                           boolean ignoreDefaultValue, String... ignoreAttributeNames) {
        this.delegate = new MutablePropertyValues(getAttributes(annotation, propertyResolver, ignoreDefaultValue, ignoreAttributeNames));
    }

    public AnnotationPropertyValuesAdapter(Annotation annotation, PropertyResolver propertyResolver, String... ignoreAttributeNames) {
        this(annotation, propertyResolver, true, ignoreAttributeNames);
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return delegate.getPropertyValues();
    }

    @Override
    public PropertyValue getPropertyValue(String propertyName) {
        return delegate.getPropertyValue(propertyName);
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        return delegate.changesSince(old);
    }

    @Override
    public boolean contains(String propertyName) {
        return delegate.contains(propertyName);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}
