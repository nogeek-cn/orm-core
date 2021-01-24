

`Mybatis-plus` 初始化， add mapper 触发，表的信息初始化，触发了，`TableInfo` 的初始化， `TableInfo` 的初始化，触发了 `Function` Lambda 表达式的加载，使用弱引用，使得信息可以回收。，Lambda 序列化，反序列化可以拿到名字，然后去 匹配 column



----







### Function 转化成 column

```java
    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        return getColumn(LambdaUtils.resolve(column), onlyColumn);
    }
```



### `com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper#getColumn`  

```java
/**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @param lambda     lambda 表达式
     * @param onlyColumn 如果是，结果: "name", 如果否： "name" as "name"
     * @return 列
     * @throws com.baomidou.mybatisplus.core.exceptions.MybatisPlusException 获取不到列信息时抛出异常
     * @see SerializedLambda#getImplClass()
     * @see SerializedLambda#getImplMethodName()
     */
    private String getColumn(SerializedLambda lambda, boolean onlyColumn) throws MybatisPlusException {
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Class aClass = lambda.getInstantiatedMethodType();
        if (!initColumnMap) {
            columnMap = LambdaUtils.getColumnMap(aClass);
        }
        Assert.notNull(columnMap, "can not find lambda cache for this entity [%s]", aClass.getName());
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        Assert.notNull(columnCache, "can not find lambda c该方法仅能传ache for this property [%s] of entity [%s]",
            fieldName, aClass.getName());
        return onlyColumn ? columnCache.getColumn() : columnCache.getColumnSelect();
    }
```





### `com.baomidou.mybatisplus.core.toolkit.LambdaUtils#resolve`



```java


    /**
     * 解析 lambda 表达式, 该方法只是调用了 {@link SerializedLambda#resolve(SFunction)} 中的方法，在此基础上加了缓存。
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     * @see SerializedLambda#resolve(SFunction)
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
            .map(WeakReference::get)
            .orElseGet(() -> {
                SerializedLambda lambda = SerializedLambda.resolve(func);
                FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                return lambda;
            });
    }
```



### Lambda 序列化

```java

/**
     * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
public static SerializedLambda resolve(SFunction<?, ?> lambda) {
    if (!lambda.getClass().isSynthetic()) {
        throw ExceptionUtils.mpe("该方法仅能传入 lambda 表达式产生的合成类");
    }
    try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(SerializationUtils.serialize(lambda))) {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            Class<?> clazz = super.resolveClass(objectStreamClass);
            return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
        }
    }) {
        return (SerializedLambda) objIn.readObject();
    } catch (ClassNotFoundException | IOException e) {
        throw ExceptionUtils.mpe("This is impossible to happen", e);
    }
}
```



### `TableInfo` 的触发条件 ，构造表的初始化信息





```java
@Override
    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            loadXmlResource();
            configuration.addLoadedResource(resource);
            final String typeName = type.getName();
            assistant.setCurrentNamespace(typeName);
            parseCache();
            parseCacheRef();
            SqlParserHelper.initSqlParserInfoCache(type);
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                try {
                    // issue #237
                    if (!method.isBridge()) {
                        parseStatement(method);
                        SqlParserHelper.initSqlParserInfoCache(typeName, method);
                    }
                } catch (IncompleteElementException e) {
                    // TODO 使用 MybatisMethodResolver 而不是 MethodResolver
                    configuration.addIncompleteMethod(new MybatisMethodResolver(this, method));
                }
            }
            // TODO 注入 CURD 动态 SQL , 放在在最后, because 可能会有人会用注解重写sql
            if (GlobalConfigUtils.isSupperMapperChildren(configuration, type)) {
                GlobalConfigUtils.getSqlInjector(configuration).inspectInject(assistant, type);
            }
        }
        parsePendingMethods();
    }
```



### `com.baomidou.mybatisplus.core.injector.AbstractSqlInjector` 

```java

    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        Class<?> modelClass = extractModelClass(mapperClass);
        if (modelClass != null) {
            String className = mapperClass.toString();
            Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
            if (!mapperRegistryCache.contains(className)) {
                List<AbstractMethod> methodList = this.getMethodList(mapperClass);
                if (CollectionUtils.isNotEmpty(methodList)) {
                    TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
                    // 循环注入自定义方法
                    methodList.forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
                } else {
                    logger.debug(mapperClass.toString() + ", No effective injection method was found.");
                }
                mapperRegistryCache.add(className);
            }
        }
    }

```



```
TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
```

### `TableInfoHelper#initTableName` 

```java
/**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        TableInfo tableInfo = TABLE_INFO_CACHE.get(clazz);
        if (tableInfo != null) {
            if (builderAssistant != null) {
                tableInfo.setConfiguration(builderAssistant.getConfiguration());
            }
            return tableInfo;
        }

        /* 没有获取到缓存信息,则初始化 */
        tableInfo = new TableInfo(clazz);
        GlobalConfig globalConfig;
        if (null != builderAssistant) {
            tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
            tableInfo.setConfiguration(builderAssistant.getConfiguration());
            globalConfig = GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration());
        } else {
            // 兼容测试场景
            globalConfig = GlobalConfigUtils.defaults();
        }

        /* 初始化表名相关 */
        initTableName(clazz, globalConfig, tableInfo);

        /* 初始化字段相关 */
        initTableFields(clazz, globalConfig, tableInfo);

        /* 放入缓存 */
        TABLE_INFO_CACHE.put(clazz, tableInfo);

        /* 缓存 lambda */
        LambdaUtils.installCache(tableInfo);

        /* 自动构建 resultMap */
        tableInfo.initResultMapIfNeed();

        return tableInfo;
    }
```





### `TableInfoHelper#initTableFields` 





```java
 /**
     * <p>
     * 初始化 表主键,表字段
     * </p>
     *
     * @param clazz        实体类
     * @param globalConfig 全局配置
     * @param tableInfo    数据库表反射信息
     */
    private static void initTableFields(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo, List<String> excludeProperty) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        ReflectorFactory reflectorFactory = tableInfo.getConfiguration().getReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(clazz);
        List<Field> list = getAllFields(clazz);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @TableId 注解
        boolean existTableId = isExistTableId(list);
        // 是否存在 @TableLogic 注解
        boolean existTableLogic = isExistTableLogic(list);

        List<TableFieldInfo> fieldList = new ArrayList<>(list.size());
        for (Field field : list) {
            if (excludeProperty.contains(field.getName())) {
                continue;
            }

            /* 主键ID 初始化 */
            if (existTableId) {
                TableId tableId = field.getAnnotation(TableId.class);
                if (tableId != null) {
                    if (isReadPK) {
                        throw ExceptionUtils.mpe("@TableId can't more than one in Class: \"%s\".", clazz.getName());
                    } else {
                        initTableIdWithAnnotation(dbConfig, tableInfo, field, tableId, reflector);
                        isReadPK = true;
                        continue;
                    }
                }
            } else if (!isReadPK) {
                isReadPK = initTableIdWithoutAnnotation(dbConfig, tableInfo, field, reflector);
                if (isReadPK) {
                    continue;
                }
            }
            final TableField tableField = field.getAnnotation(TableField.class);

            /* 有 @TableField 注解的字段初始化 */
            if (tableField != null) {
                fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, tableField, reflector, existTableLogic));
                continue;
            }

            /* 无 @TableField 注解的字段初始化 */
            fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, reflector, existTableLogic));
        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，提示警告信息 */
        if (!isReadPK) {
            logger.warn(String.format("Can not find table primary key in Class: \"%s\".", clazz.getName()));
        }
    }
```











# JPA

```java
/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.support;

import static org.springframework.data.jpa.util.BeanDefinitionUtils.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.util.BeanDefinitionUtils.EntityManagerFactoryBeanDefinition;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

/**
 * {@link BeanFactoryPostProcessor} to register a {@link SharedEntityManagerCreator} for every
 * {@link EntityManagerFactory} bean definition found in the application context to enable autowiring
 * {@link EntityManager} instances into constructor arguments. Adds the {@link EntityManagerFactory} bean name as
 * qualifier to the {@link EntityManager} {@link BeanDefinition} to enable explicit references in case of multiple
 * {@link EntityManagerFactory} instances.
 *
 * @author Oliver Gierke
 */
public class EntityManagerBeanDefinitionRegistrarPostProcessor implements BeanFactoryPostProcessor, Ordered {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		if (!ConfigurableListableBeanFactory.class.isInstance(beanFactory)) {
			return;
		}

		ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) beanFactory;

		for (EntityManagerFactoryBeanDefinition definition : getEntityManagerFactoryBeanDefinitions(factory)) {

			BeanFactory definitionFactory = definition.getBeanFactory();

			if (!(definitionFactory instanceof BeanDefinitionRegistry)) {
				continue;
			}

			BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) definitionFactory;

			BeanDefinitionBuilder builder = BeanDefinitionBuilder
					.rootBeanDefinition("org.springframework.orm.jpa.SharedEntityManagerCreator");
			builder.setFactoryMethod("createSharedEntityManager");
			builder.addConstructorArgReference(definition.getBeanName());

			AbstractBeanDefinition emBeanDefinition = builder.getRawBeanDefinition();

			emBeanDefinition.addQualifier(new AutowireCandidateQualifier(Qualifier.class, definition.getBeanName()));
			emBeanDefinition.setScope(definition.getBeanDefinition().getScope());
			emBeanDefinition.setSource(definition.getBeanDefinition().getSource());
			emBeanDefinition.setLazyInit(true);

			BeanDefinitionReaderUtils.registerWithGeneratedName(emBeanDefinition, definitionRegistry);
		}
	}
}

```





```java
/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Configurable {@link FieldNamingStrategy} that splits up camel-case property names and reconcatenates them using a
 * configured delimiter. Individual parts of the name can be manipulated using {@link #preparePart(String)}.
 * 
 * @author Oliver Gierke
 * @since 1.9
 */
public class CamelCaseSplittingFieldNamingStrategy implements FieldNamingStrategy {

	private final String delimiter;

	/**
	 * Creates a new {@link CamelCaseSplittingFieldNamingStrategy}.
	 * 
	 * @param delimiter must not be {@literal null}.
	 */
	public CamelCaseSplittingFieldNamingStrategy(String delimiter) {

		Assert.notNull(delimiter, "Delimiter must not be null!");
		this.delimiter = delimiter;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.mapping.model.FieldNamingStrategy#getFieldName(org.springframework.data.mapping.PersistentProperty)
	 */
	@Override
	public String getFieldName(PersistentProperty<?> property) {

		List<String> parts = ParsingUtils.splitCamelCaseToLower(property.getName());
		List<String> result = new ArrayList<String>();

		for (String part : parts) {

			String candidate = preparePart(part);

			if (StringUtils.hasText(candidate)) {
				result.add(candidate);
			}
		}

		return StringUtils.collectionToDelimitedString(result, delimiter);
	}

	/**
	 * Callback to prepare the uncapitalized part obtained from the split up of the camel case source. Default
	 * implementation returns the part as is.
	 * 
	 * @param part
	 * @return
	 */
	protected String preparePart(String part) {
		return part;
	}
}

```



```java
/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Utility methods for {@link String} parsing.
 * 
 * @author Oliver Gierke
 * @since 1.5
 */
public abstract class ParsingUtils {

	private static final String UPPER = "\\p{Lu}|\\P{InBASIC_LATIN}";
	private static final String LOWER = "\\p{Ll}";
	private static final String CAMEL_CASE_REGEX = "(?<!(^|[%u_$]))(?=[%u])|(?<!^)(?=[%u][%l])". //
			replace("%u", UPPER).replace("%l", LOWER);

	private static final Pattern CAMEL_CASE = Pattern.compile(CAMEL_CASE_REGEX);

	private ParsingUtils() {}

	/**
	 * Splits up the given camel-case {@link String}.
	 * 
	 * @param source must not be {@literal null}.
	 * @return
	 */
	public static List<String> splitCamelCase(String source) {
		return split(source, false);
	}

	/**
	 * Splits up the given camel-case {@link String} and returns the parts in lower case.
	 * 
	 * @param source must not be {@literal null}.
	 * @return
	 */
	public static List<String> splitCamelCaseToLower(String source) {
		return split(source, true);
	}

	/**
	 * Reconcatenates the given camel-case source {@link String} using the given delimiter. Will split up the camel-case
	 * {@link String} and use an uncapitalized version of the parts.
	 * 
	 * @param source must not be {@literal null}.
	 * @param delimiter must not be {@literal null}.
	 * @return
	 */
	public static String reconcatenateCamelCase(String source, String delimiter) {

		Assert.notNull(source, "Source string must not be null!");
		Assert.notNull(delimiter, "Delimiter must not be null!");

		return StringUtils.collectionToDelimitedString(splitCamelCaseToLower(source), delimiter);
	}

	private static List<String> split(String source, boolean toLower) {

		Assert.notNull(source, "Source string must not be null!");

		String[] parts = CAMEL_CASE.split(source);
		List<String> result = new ArrayList<String>(parts.length);

		for (String part : parts) {
			result.add(toLower ? part.toLowerCase() : part);
		}

		return Collections.unmodifiableList(result);
	}
}

```

