# ORM_core 

## 简介

- 简单的 orm 映射关系
- 理论上最快，最核心，不与任何数据库强绑定，只强调映射关系，可以与任何数据库GAV进行集成
- 缺点：绑定了 Spring
- github:
  - <a href="https://github.com/Darian1996/orm-core" target="_blank">github_rep</a>
- gitee:
  - <a href="https://gitee.com/Darian1996/orm-core" target="_blank">gitee_rep</a>


## 设计初衷 

- [Why](/Why.md) 

## 为什么要用这款框架 

1. 支持标准 `persistence-api` 的注解。
2. 没有学习成本：只需要 引入 `@TableInfo` 注解就可以实现映射。
3. 支持扩展，字段名与数据库名字对应关系可以自定义。是否初始化。
4. **支持 Function 编程，`Function` 与 `fieldName` 也进行了缓存。**  例如：`#getUserName` 与 `userName` 的对应关系。
5. **快，无任何反射运行时的消耗！！！**
6. 不想引入其他的依赖，又不想使用便捷的 API，（你引入 spring-data-jpa ，就立马绑定上了，而且还没有 Function 与 column 的对应关系）
7. 高扩展性，可以自定义实现 `fieldName` 与 `columnName` 对应关系，比如，你们的表明开头都有 "xxx_" 这种，也很容易做到。

## Feature

- 基于 Spring xml 的扩展解析，还没做，只是做了注解的。。。。。。


### Tips

- 这里边大量代码来自于 `Dubbo` ，模仿着写了一边。工具类不需要测试了，反正 `Dubbo` 都测试过了。
- JAVA字段名与数据库字段名 copy from `spring data jpa` 。 
- `Lambda` 与字段名对应关系来自于 `mybatis-plus`  。



## 例子

引入：

- 还不会往中心仓库发 :cry: :cry: :cry:  :cry: ​

```xml
<dependency>
    <groupId>top.darian</groupId>
    <artifactId>orm-core-spring</artifactId>
</dependency>
```



### Java使用

```java
@SpringBootApplication
@TableInfoComponentScan("top.darian.orm.core.example")
public class OrmCoreExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrmCoreExampleApplication.class, args);
    // user_name
    System.out.println(
            BeanToDataBaseUtils.getColumnByFunctionName(
                    TestModule::getUserName,
                    TestModule.class));
  }
}
@TableInfo
@Data
class TestModule {
  private String userName;
}
```

### 自定义扩展

```properties
# 系统启动的时候是否把 对应关系 初始化
#top.darian.tableInfoServiceBean.earlyInitialization=true
#top.darian.tableInfoServiceBean.earlyInitialization=false
# 全局 fieldName 和 column 对应关系 默认策略： top.darian.orm.core.spring.util.module.mapping.SnakeCaseFieldNamingStrategy
#top.darian.orm.core.spring.util.module.mapping.FieldNamingStrategy=classFullName
# 全局 entity.classSimpleName 和 tableName 对应关系 默认策略： top.darian.orm.core.spring.util.module.mapping.SnakeCaseFieldNamingStrategy
#top.darian.orm.core.spring.util.module.mapping.tableNamingStrategy=classFullName

```





