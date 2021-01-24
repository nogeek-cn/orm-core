package top.darian.orm.core.spring.beans.context.annotation;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午8:55
 */

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors;

/**
 * tableInfo {@link ClassPathBeanDefinitionScanner} that exposes some methods to be public.
 *
 * @see #doScan(String...)
 * @see #registerDefaultFilters()
 * @since 2.5.7
 */
public class TableInfoClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {


    public TableInfoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment,
                                                   ResourceLoader resourceLoader) {

        super(registry, useDefaultFilters);

        setEnvironment(environment);

        setResourceLoader(resourceLoader);

        registerAnnotationConfigProcessors(registry);

    }

    public TableInfoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, Environment environment,
                                                   ResourceLoader resourceLoader) {

        this(registry, false, environment, resourceLoader);

    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        return super.checkCandidate(beanName, beanDefinition);
    }

}
