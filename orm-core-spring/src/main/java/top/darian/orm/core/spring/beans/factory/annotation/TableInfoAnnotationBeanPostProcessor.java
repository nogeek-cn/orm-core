package top.darian.orm.core.spring.beans.factory.annotation;


import com.alibaba.spring.beans.factory.annotation.AbstractAnnotationBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAttributes;
import top.darian.orm.core.config.annotation.TableInfo;
import top.darian.orm.core.config.spring.beans.factory.annotation.TableInfoServiceBeanNameBuilder;
import top.darian.orm.core.spring.TableInfoServiceBean;

import java.util.Map;

import static com.alibaba.spring.util.AnnotationUtils.getAttributes;
import static top.darian.orm.core.config.spring.beans.factory.annotation.TableInfoServiceBeanNameBuilder.create;

/***
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that Consumer service {@link Reference} annotated fields
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午4:01
 */
public class TableInfoAnnotationBeanPostProcessor
        extends AbstractAnnotationBeanPostProcessor
        implements ApplicationContextAware {

    /**
     * The bean name of {@link TableInfoAnnotationBeanPostProcessor}
     */
    public static final String BEAN_NAME = "tableInfoAnnotationBeanPostProcessor";

    /**
     * Cache size
     */
    private static final int CACHE_SIZE = Integer.getInteger(BEAN_NAME + ".cache.size", 32);

    private ApplicationContext applicationContext;

    /**
     * {@link TableInfo } has been supported since 2.7.3
     * <p>
     * {@link TableInfo @TableInfo} has been supported since 2.7.7
     */
    public TableInfoAnnotationBeanPostProcessor() {
        super(TableInfo.class);
    }


    @Override
    protected String buildInjectedObjectCacheKey(AnnotationAttributes attributes, Object bean, String beanName,
                                                 Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) {
        return buildReferencedBeanName(attributes, injectedType) +
                "#source=" + (injectedElement.getMember()) +
                "#attributes=" + getAttributes(attributes, getEnvironment());
    }

    /**
     * @param attributes           the attributes of {@link Reference @Reference}
     * @param serviceInterfaceType the type of TableInfo's service interface
     * @return The name of bean that annotated TableINfo's {@link Service @Service} in local Spring {@link ApplicationContext}
     */
    private String buildReferencedBeanName(AnnotationAttributes attributes, Class<?> serviceInterfaceType) {
        TableInfoServiceBeanNameBuilder serviceBeanNameBuilder = create(attributes, serviceInterfaceType);
        return serviceBeanNameBuilder.build();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    protected Object doGetInjectedBean(AnnotationAttributes annotationAttributes, Object o, String s, Class<?> aClass, InjectionMetadata.InjectedElement injectedElement) throws Exception {
        // delete some codeing
        return null;
    }
}
