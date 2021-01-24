# `ORM_core`

## 设计初衷

- 某些公司（尤其是大公司）会进行自研一些数据库（关系型数据库，非关系型数据库），只有特定的语法，却并没有类似于 `JPA`、`Mybatis` 一样的 **事实标准**  进行支撑。
- 他们强悍的底层设计能力，却并没有应用在应用层的抽象之中。经常性的破坏性升级 `API`，每次的迭代升级需要强制的学习新的语法。
- 为了适配各种的奇葩数据库，本人想研发一套 `simple_ORM` 框架作为内核。
- 然后基于本内核做 基础架构组提供的 `API` 做更上一层抽象 `API` 层面的封装。
- 还可以支持 `HBase` 等等的 `ORM` 映射，只要基于本内核和任何数据库 `API` 做一层抽象，就可以了。
- 市面上多数都是 与数据库强绑定，而且都做的是 `fieldName` 和 `columnName` 显示对应，还是比较容易出错。
- 增加 `Function` 与 `columnName` 的对应关系。

## 设计目标

- 只做 `Java` `fieldName` 和 `DataBase` `column` 映射的内核。
    - 至于，你们不同数据库特定语法的封装，需要自行的在这个之上进行再次研发。这个受限于 自研数据库的 抽象能力。如果，他们做了，我这里就不需要做了
- 兼容性
    - 使用 `JPA` 的注解，并且为了避免其他 原有的 `DataBase` 比如存量加了的 `JPA` 的 `Mysql` 模型，不需要注册到 Spring `IOC` 容器中，那么就需要，所以默认不注册。
        - `@SimpleTableInfo` 这个注解的，才进行扫描。
- `Simple`
    - 使用简单，类似于 `JPA` 一样，实现大小写自动转化、自动表名的映射、、、等等
    - 还希望引入 `Mybatis-plus` 的 `Function` 与 `fieldName` 的映射关系
- 性能
    - 不在反射时做。（将 每一个 `TableInfo` 注册成为一个 Spring Bean :  `TableInfoService`  [这个模仿 Dubbo]）（除了 `Function` 和 `fieldName` [ 想支持函数式关系就需要维护，方法与字段名的映射。这个对内存的要求，还没有考虑过 ]），
- 设计思想
    - 基于 Spring 事件机制，模仿 `Dubbo` 的内核做。
        - 配置文件加载完成
        - 扫描注解
        - 生成 `TableInfoService` 元信息
        - 注册到 Spring Bean
        - `TableInfoBootStrap`  `#start()` 事件，
            - 基于 `配置信息` 实现外部化是否初始化 `TableInfoServiceBean`
            - 是在延迟初始化，直接启动 `TableInfoServiceBean` 初始化
                - 预先加载 `TableInfoService` 的时候是否阻塞。（这个要确认 Spring 的 `getBean()` 是否安全 ）



## Question

- 除了 `Function` 和 `fieldName` [ 想支持函数式关系就需要维护，方法与字段名的映射。这个对内存的要求，还没有考虑过

`Mybatis-plus` 的 `Function` 和 `fieldName` 对应关系，是这么存储的我将这个放在 `TableInfoServiceBean` ( Spring Bean ) 中是否合理，不合理的话，这个我运行时去转化。

```java
    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<Class<?>, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();
```

```java
package com.darian.darianlucenefile;

import java.util.function.Function;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午5:36
 */
public class AAA {
    public static void main(String[] args) {
        Function<String, String> stringStringFunction1 = TestModule.testUsedFunction();
        System.out.println(stringStringFunction1);

        Function<String, String> stringStringFunction2 = TestModule.testUsedFunction();
        System.out.println(stringStringFunction2);

        System.out.println(stringStringFunction1.equals(stringStringFunction2));
        // true

    }
}


class TestModule {
    private String named;


    public static String getNamed(String s) {
        return null;
    }

    public static Function<String, String> testUsedFunction() {
        Function<String, String> getNamed = TestModule::getNamed;
        System.out.println(getNamed);
        return getNamed;
    }
}
```



说明 `Function` 同一个在内存中只会实例化一次。以内置类的形式初始化。

### so

那我们就维护一个 `FunctionName` 与 `fieldName` 的映射吧。