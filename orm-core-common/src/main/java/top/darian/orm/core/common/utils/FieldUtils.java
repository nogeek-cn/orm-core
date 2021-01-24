package top.darian.orm.core.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午10:15
 */
public class FieldUtils {


    /**
     * Like the {@link Class#getDeclaredField(String)} method without throwing any {@link Exception}
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    static Field getDeclaredField(Class<?> declaredClass, String fieldName) {
        Field field = null;
        try {
            field = declaredClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            field = null;
        }
        return field;
    }

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    static Field findField(Class<?> declaredClass, String fieldName) {
        Field field = getDeclaredField(declaredClass, fieldName);
        if (field != null) {
            return field;
        }
        for (Class superType : ClassUtils.getAllInheritedTypes(declaredClass)) {
            field = getDeclaredField(superType, fieldName);
            if (field != null) {
                break;
            }
        }
        return field;
    }

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    static Field findField(Object object, String fieldName) {
        return findField(object.getClass(), fieldName);
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return the value of  the specified {@link Field}
     */
    static Object getFieldValue(Object object, String fieldName) {
        return getFieldValue(object, findField(object, fieldName));
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @return the value of  the specified {@link Field}
     */
    static <T> T getFieldValue(Object object, Field field) {
        boolean accessible = field.isAccessible();
        Object value = null;
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            value = field.get(object);
        } catch (IllegalAccessException ignored) {
        } finally {
            field.setAccessible(accessible);
        }
        return (T) value;
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @param value     the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    static <T> T setFieldValue(Object object, String fieldName, T value) {
        return setFieldValue(object, findField(object, fieldName), value);
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @param value  the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    static <T> T setFieldValue(Object object, Field field, T value) {
        boolean accessible = field.isAccessible();
        Object previousValue = null;
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            previousValue = field.get(object);
            field.set(object, value);
        } catch (IllegalAccessException ignored) {
        } finally {
            field.setAccessible(accessible);
        }
        return (T) previousValue;
    }

    /**
     *
     * @param cls
     * @return
     * @author darian
     */
    public static List<Field> getAllFieldsList(Class<?> cls) {
        List<Field> allFields = new ArrayList();
        for (Class currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
        }
        return allFields;
    }
}
