package top.darian.orm.core.spring.beans.factory.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import top.darian.orm.core.common.constants.CommonConstants;
import top.darian.orm.core.common.utils.ArrayUtils;
import top.darian.orm.core.common.utils.ClassUtils;
import top.darian.orm.core.common.utils.FieldUtils;
import top.darian.orm.core.config.annotation.TableInfo;
import top.darian.orm.core.config.bootstrap.TableInfoBootstrap.TableInfoBootstrapApplicationListener;
import top.darian.orm.core.config.spring.beans.factory.annotation.TableInfoServiceBeanNameBuilder;
import top.darian.orm.core.spring.TableInfoServiceBean;
import top.darian.orm.core.spring.beans.context.annotation.TableInfoClassPathBeanDefinitionScanner;
import top.darian.orm.core.spring.util.module.mapping.FieldNamingStrategy;
import top.darian.orm.core.spring.util.module.mapping.SnakeCaseFieldNamingStrategy;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.annotation.Annotation;

import static com.alibaba.spring.util.BeanRegistrar.registerInfrastructureBean;
import static com.alibaba.spring.util.ObjectUtils.of;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes;
import static org.springframework.util.ClassUtils.resolveClassName;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午3:23
 */


/**
 * {@link BeanFactoryPostProcessor} used for processing of {@link TableInfo}  annotated classes. it's also the
 * infrastructure class of XML {@link BeanDefinitionParser} on &lt;dubbbo:annotation /&gt;
 *
 * @see BeanDefinitionRegistryPostProcessor
 * @since 2.7.7
 */
public class ServiceClassPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    private final static List<Class<? extends Annotation>> serviceAnnotationTypes = asList(
            TableInfo.class
    );

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Set<String> packagesToScan;


    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private FieldNamingStrategy fieldNamingStrategy;

    private FieldNamingStrategy tableNamingStrategy;

    public ServiceClassPostProcessor(String... packagesToScan) {
        this(asList(packagesToScan));
    }

    public ServiceClassPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<>(packagesToScan));
    }

    public ServiceClassPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        registerInfrastructureBean(registry,
                TableInfoBootstrapApplicationListener.BEAN_NAME,
                TableInfoBootstrapApplicationListener.class);

        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);

        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("packagesToScan is empty , ServiceBean registry will be ignored!");
            }
        }

    }

    /**
     * Registers Beans whose classes was annotated
     *
     * @param packagesToScan The base packages to scan
     * @param registry       {@link BeanDefinitionRegistry}
     */
    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        TableInfoClassPathBeanDefinitionScanner scanner =
                new TableInfoClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        scanner.setBeanNameGenerator(beanNameGenerator);

        // refactor @since 2.7.7
        serviceAnnotationTypes.forEach(annotationType -> {
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        });

        for (String packageToScan : packagesToScan) {

            // Registers @Service Bean first
            scanner.scan(packageToScan);

            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders =
                    findServiceBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerServiceBean(beanDefinitionHolder, registry, scanner);
                }

                if (logger.isInfoEnabled()) {
                    logger.info(beanDefinitionHolders.size() + " annotated TableInfo's @TableInfo Components { " +
                            beanDefinitionHolders +
                            " } were scanned under package[" + packageToScan + "]");
                }

            } else {

                if (logger.isWarnEnabled()) {
                    logger.warn("No Spring Bean annotating TableInfo's @TableInfo was found under package["
                            + packageToScan + "]");
                }

            }

        }

    }

    /**
     * It'd better to use BeanNameGenerator instance that should reference
     * {@link ConfigurationClassPostProcessor#componentScanBeanNameGenerator},
     * thus it maybe a potential problem on bean name generation.
     *
     * @param registry {@link BeanDefinitionRegistry}
     * @return {@link BeanNameGenerator} instance
     * @see SingletonBeanRegistry
     * @see AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
     * @see ConfigurationClassPostProcessor#processConfigBeanDefinitions
     * @since 2.5.8
     */
    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {

            if (logger.isInfoEnabled()) {

                logger.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
                        + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
                logger.info("BeanNameGenerator will be a instance of " +
                        AnnotationBeanNameGenerator.class.getName() +
                        " , it maybe a potential problem on bean name generation.");
            }

            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }

    /**
     * Finds a {@link Set} of {@link BeanDefinitionHolder BeanDefinitionHolders} whose bean type annotated
     * {@link TableInfo} Annotation.
     *
     * @param scanner       {@link ClassPathBeanDefinitionScanner}
     * @param packageToScan pachage to scan
     * @param registry      {@link BeanDefinitionRegistry}
     * @return non-null
     * @since 2.5.8
     */
    private Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());

        for (BeanDefinition beanDefinition : beanDefinitions) {

            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);

        }

        return beanDefinitionHolders;

    }

    /**
     * Registers {@link TableInfoServiceBean} from new annotated {@link TableInfoServiceBean} {@link BeanDefinition}
     *
     * @param beanDefinitionHolder
     * @param registry
     * @param scanner
     * @see TableInfoServiceBean
     * @see BeanDefinition
     */
    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     TableInfoClassPathBeanDefinitionScanner scanner) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);

        Annotation service = findServiceAnnotation(beanClass);

        /**
         * The {@link AnnotationAttributes} of @Service annotation
         */
        AnnotationAttributes serviceAnnotationAttributes = getAnnotationAttributes(service, false, false);

        // this ==
        Class<?> interfaceClass = beanClass;

        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();

        AbstractBeanDefinition serviceBeanDefinition =
                buildServiceBeanDefinition(service, serviceAnnotationAttributes, interfaceClass, annotatedServiceBeanName);

        // ServiceBean Bean name
        String beanName = generateServiceBeanName(serviceAnnotationAttributes, interfaceClass);

        if (scanner.checkCandidate(beanName, serviceBeanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(beanName, serviceBeanDefinition);

            if (logger.isInfoEnabled()) {
                logger.info("The BeanDefinition[" + serviceBeanDefinition +
                        "] of ServiceBean has been registered with name : " + beanName);
            }

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + serviceBeanDefinition +
                        "] of ServiceBean[ bean name : " + beanName +
                        "] was be found , Did @TableInfoComponentScan scan to same package in many times?");
            }

        }

    }

    /**
     * Find the {@link Annotation annotation} of @Service
     *
     * @param beanClass the {@link Class class} of Bean
     * @return <code>null</code> if not found
     * @since 2.7.3
     */
    private Annotation findServiceAnnotation(Class<?> beanClass) {
        return serviceAnnotationTypes
                .stream()
                .map(annotationType -> findMergedAnnotation(beanClass, annotationType))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates the bean name of {@link TableInfoServiceBean}
     *
     * @param serviceAnnotationAttributes
     * @param interfaceClass              the class of interface annotated {@link TableInfo}
     * @return ServiceBean@interfaceClassName#annotatedServiceBeanName
     * @since 2.7.3
     */
    private String generateServiceBeanName(AnnotationAttributes serviceAnnotationAttributes, Class<?> interfaceClass) {
        TableInfoServiceBeanNameBuilder builder = TableInfoServiceBeanNameBuilder.create(interfaceClass);
        return builder.build();
    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {

        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);

    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    /**
     * Build the {@link AbstractBeanDefinition Bean Definition}
     *
     * @param serviceAnnotation
     * @param serviceAnnotationAttributes
     * @param interfaceClass
     * @param annotatedServiceBeanName
     * @return
     * @since 2.7.3
     */
    private AbstractBeanDefinition buildServiceBeanDefinition(Annotation serviceAnnotation,
                                                              AnnotationAttributes serviceAnnotationAttributes,
                                                              Class<?> interfaceClass,
                                                              String annotatedServiceBeanName) {

        BeanDefinitionBuilder builder = rootBeanDefinition(TableInfoServiceBean.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

//        String[] ignoreAttributeNames = of("provider", "monitor", "application", "module", "registry", "protocol",
//                "interface", "interfaceName", "parameters");
        String[] ignoreAttributeNames = of();

        propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(serviceAnnotation, environment, ignoreAttributeNames));

        // References "ref" property to annotated-@Service Bean
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        // Set tableClassName
        builder.addPropertyValue("tableClassName", interfaceClass.getName());

        String tableNameValue = null;
        Table tableAnnotation = interfaceClass.getAnnotation(Table.class);
        if (Objects.isNull(tableAnnotation) || StringUtils.isEmpty(tableAnnotation.name())) {
            tableNameValue = tableNamingStrategy.fieldNameToColumnName(interfaceClass.getSimpleName());
        } else {
            tableNameValue = tableAnnotation.name();
        }

        builder.addPropertyValue("tableName", tableNameValue);

        Map<String, String> fieldColumnMap = new HashMap<>();

        // 这里添加表映射关系
        List<Field> allFieldsList = FieldUtils.getAllFieldsList(interfaceClass);
        for (Field field : allFieldsList) {
            String fieldName = field.getName();
            if (StringUtils.isEmpty(fieldName)) {
                logger.warn(String.format("[%s] bean , fieldName is empty, field [%s]",
                        annotatedServiceBeanName, field));
            }
            final Column fieldColumnAnnotation = field.getAnnotation(Column.class);

            String columnName = "";
            if (Objects.isNull(fieldColumnAnnotation) || StringUtils.isEmpty(fieldColumnAnnotation.name())) {
                columnName = fieldNamingStrategy.fieldNameToColumnName(fieldName);
            } else {
                columnName = fieldColumnAnnotation.name();
            }
            fieldColumnMap.put(fieldName, columnName);
        }


        builder.addPropertyValue("fieldColumnMap", fieldColumnMap);

        return builder.getBeanDefinition();
    }


    private ManagedList<RuntimeBeanReference> toRuntimeBeanReferences(String... beanNames) {

        ManagedList<RuntimeBeanReference> runtimeBeanReferences = new ManagedList<>();

        if (!ObjectUtils.isEmpty(beanNames)) {

            for (String beanName : beanNames) {

                String resolvedBeanName = environment.resolvePlaceholders(beanName);

                runtimeBeanReferences.add(new RuntimeBeanReference(resolvedBeanName));
            }

        }

        return runtimeBeanReferences;

    }

    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }

    private Map<String, String> convertParameters(String[] parameters) {
        if (ArrayUtils.isEmpty(parameters)) {
            return null;
        }

        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("parameter attribute must be paired with key followed by value");
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i += 2) {
            map.put(parameters[i], parameters[i + 1]);
        }
        return map;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        String fieldNaming = environment.getProperty(CommonConstants.TABLE_INFO_FIELD_NAMING_STRATEGY);
        fieldNamingStrategy = getFieldNamingStrategyByClassNameString(fieldNaming);
        if (Objects.isNull(fieldNamingStrategy)) {
            logger.info("fieldNamingStrategy user the SnakeCaseFieldNamingStrategy");
            fieldNamingStrategy = new SnakeCaseFieldNamingStrategy();
        }

        String tableNaming = environment.getProperty(CommonConstants.TABLE_INFO_TABLE_NAMING_STRATEGY);
        tableNamingStrategy = getFieldNamingStrategyByClassNameString(tableNaming);
        if (Objects.isNull(tableNamingStrategy)) {
            logger.info("tableNamingStrategy user the SnakeCaseFieldNamingStrategy");
            tableNamingStrategy = new SnakeCaseFieldNamingStrategy();
        }


    }

    private FieldNamingStrategy getFieldNamingStrategyByClassNameString(String className) {
        if (StringUtils.hasText(className)) {
            Class<?> aClass = null;
            try {
                aClass = ClassUtils.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("customer fieldNamingStrategy className not find the class...", e);
            }
            if (Objects.nonNull(aClass)) {
                try {
                    return (FieldNamingStrategy) aClass.newInstance();
                } catch (Exception e) {
                    logger.error("customer fieldNamingStrategy new Instance() fail...", e);
                }
            }
        }
        return null;
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

