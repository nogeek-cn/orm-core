package top.darian.orm.core.spring.beans.context.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import org.springframework.context.annotation.Import;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午3:18
 */

/**
 * TableInfo Component Scan {@link Annotation},scans the classpath for annotated components that will be auto-registered as
 * Spring beans. TableInfo-provided {@link top.darian.orm.core.config.annotation.TableInfo} .
 *
 * @see top.darian.orm.core.config.annotation.TableInfo
 * @since 2.5.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TableInfoComponentScanRegistrar.class)
public @interface TableInfoComponentScan {


    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @TableInfoComponentScan("org.my.pkg")} instead of
     * {@code @TableInfoComponentScan(basePackages="org.my.pkg")}.
     *
     * @return the base packages to scan
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated @Service classes. {@link #value()} is an
     * alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the base packages to scan
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated @Service classes. The package of each class specified will be
     * scanned.
     *
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};

}
