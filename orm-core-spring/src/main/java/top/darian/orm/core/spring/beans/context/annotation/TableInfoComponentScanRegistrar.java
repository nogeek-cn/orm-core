package top.darian.orm.core.spring.beans.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


import top.darian.orm.core.config.spring.util.TableInfoCommonBeanUtils;
import top.darian.orm.core.spring.beans.factory.annotation.ServiceClassPostProcessor;
import top.darian.orm.core.spring.beans.factory.annotation.TableInfoAnnotationBeanPostProcessor;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;


/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午9:15
 */

/**
 * {@link TableInfoComponentScan} Bean Registrar
 *
 * @see top.darian.orm.core.config.annotation.TableInfo
 * @see TableInfoComponentScan
 * @see ImportBeanDefinitionRegistrar
 * @see
 * @see TableInfoAnnotationBeanPostProcessor
 * @since 2.5.7
 */
public class TableInfoComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);

        registerServiceAnnotationBeanPostProcessor(packagesToScan, registry);

        // @since 2.7.6 Register the common beans
        TableInfoCommonBeanUtils.registerCommonBeans(registry);
    }

    /**
     * Registers {@link ServiceClassPostProcessor}
     *
     * @param packagesToScan packages to scan without resolving placeholders
     * @param registry       {@link BeanDefinitionRegistry}
     * @since 2.5.8
     */
    private void registerServiceAnnotationBeanPostProcessor(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceClassPostProcessor.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);

    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(TableInfoComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] value = attributes.getStringArray("value");
        // Appends value array attributes
        Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty()) {
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }

}
