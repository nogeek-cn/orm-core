# ORM_core 

## 设计初衷 

- 简单的 orm 映射关系
- github: 
  - https://github.com/Darian1996/orm-core
- gitee: 
  - https://gitee.com/Darian1996/orm-core

[Why](/Why.md) 

## 为什么要用这款框架 

1. 支持标准 `persistence-api` 的注解。
2. 没有学习成本：只需要 引入 `@TableInfo` 注解就可以实现映射。
3. 支持扩展，字段名与数据库名字对应关系可以自定义。是否初始化。
4. **支持 Function 编程，`Function` 与 `fieldName` 也进行了缓存。**  例如：`#getUserName` 与 `userName` 的对应关系。



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

class TestClass {
    public void test() {
        testOne(SchoolDO.class);
        testOne(UserDO.class);
        testOne(ClassDO.class);

        testField();
        testField();
        testFunction();
        testFunction();
        testFunction();

        System.out.println(TableInfoBeanUtils.getBean(UserDO.class).getFunctionColumnMap());
        System.out.println("UserDO.functionColumnMap().size():\t"
                + TableInfoBeanUtils.getBean(UserDO.class).getFunctionColumnMap().size());
    }


    private void testOne(Class<?> clazz) {
        TableInfoServiceBean<?> bean = TableInfoBeanUtils.getBean(clazz);


        System.out.println();
        System.out.println(String.format("tableInfoService.ref[%s]:beanToString\t[%s]", clazz.getSimpleName(), bean));
        System.out.println(String.format("tableInfoService.ref[%s].interfaceName:\t[%s]", clazz.getSimpleName(), bean.getTableClassName()));
        System.out.println(String.format("tableInfoService.ref[%s].interfaceClass:\t[%s]", clazz.getSimpleName(), bean.getTableClassClass()));
        System.out.println(String.format("tableInfoService.ref[%s].fieldColumnMap:\t[%s]", clazz.getSimpleName(), bean.getFieldColumnMap()));
        System.out.println(String.format("tableInfoService.ref[%s].functionFieldMap:\t[%s]", clazz.getSimpleName(), bean.getFunctionColumnMap()));
        System.out.println(String.format("tableInfoService.ref[%s].tableName:\t[%s]", clazz.getSimpleName(), bean.getTableName()));


    }


    public void testFunction() {

        System.out.println("UserDO::getUserName\t=\t" +
                BeanToDataBaseUtils.getColumnByFunctionName(UserDO::getUserName, UserDO.class));

        System.out.println("UserDO::getName\t=\t"
                + BeanToDataBaseUtils.getColumnByFunctionName(UserDO::getName, UserDO.class)); }

    public void testField() {
        System.out.println("UserDO.name\t=\t" +
                BeanToDataBaseUtils.getColumnByFieldName("name", UserDO.class));

        System.out.println("UserDO.name\t=\t" +
                BeanToDataBaseUtils.getColumnByFieldName("userName", UserDO.class));
    }
}

```

```java
tableInfoService.ref[SchoolDO]:beanToString	[top.darian.orm.core.spring.TableInfoServiceBean@7d0d91a1]
tableInfoService.ref[SchoolDO].interfaceName:	[top.darian.orm.core.example.endity.SchoolDO]
tableInfoService.ref[SchoolDO].interfaceClass:	[class top.darian.orm.core.example.endity.SchoolDO]
tableInfoService.ref[SchoolDO].fieldColumnMap:	[{id=id, schoolName=school_name}]
tableInfoService.ref[SchoolDO].functionFieldMap:	[{}]
tableInfoService.ref[SchoolDO].tableName:	[school_do]

tableInfoService.ref[UserDO]:beanToString	[top.darian.orm.core.spring.TableInfoServiceBean@7fb48179]
tableInfoService.ref[UserDO].interfaceName:	[top.darian.orm.core.example.endity.UserDO]
tableInfoService.ref[UserDO].interfaceClass:	[class top.darian.orm.core.example.endity.UserDO]
tableInfoService.ref[UserDO].fieldColumnMap:	[{name=name, userName=user_name}]
tableInfoService.ref[UserDO].functionFieldMap:	[{}]
tableInfoService.ref[UserDO].tableName:	[user_do]

tableInfoService.ref[ClassDO]:beanToString	[top.darian.orm.core.spring.TableInfoServiceBean@201c3cda]
tableInfoService.ref[ClassDO].interfaceName:	[top.darian.orm.core.example.endity.ClassDO]
tableInfoService.ref[ClassDO].interfaceClass:	[class top.darian.orm.core.example.endity.ClassDO]
tableInfoService.ref[ClassDO].fieldColumnMap:	[{className=aaa_class_name}]
tableInfoService.ref[ClassDO].functionFieldMap:	[{}]
tableInfoService.ref[ClassDO].tableName:	[user_class]
UserDO.name	=	name
UserDO.name	=	user_name
UserDO.name	=	name
UserDO.name	=	user_name
UserDO::getUserName	=	user_name
UserDO::getName	=	name
UserDO::getUserName	=	user_name
UserDO::getName	=	name
UserDO::getUserName	=	user_name
UserDO::getName	=	name
{top.darian.orm.core.example.TestClass$$Lambda$511/0x00000001004d1840=userName, top.darian.orm.core.example.TestClass$$Lambda$512/0x00000001004d1c40=name}
UserDO.functionColumnMap().size():	2

```





`orm-core-example` 

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





