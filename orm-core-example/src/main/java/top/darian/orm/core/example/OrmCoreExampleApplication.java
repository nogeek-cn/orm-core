package top.darian.orm.core.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import top.darian.orm.core.BeanToDataBaseUtils;
import top.darian.orm.core.example.endity.ClassDO;
import top.darian.orm.core.example.endity.SchoolDO;
import top.darian.orm.core.example.endity.UserDO;
import top.darian.orm.core.spring.TableInfoServiceBean;
import top.darian.orm.core.spring.beans.context.annotation.TableInfoComponentScan;
import top.darian.orm.core.spring.beans.util.TableInfoBeanUtils;


@SpringBootApplication
@TableInfoComponentScan("top.darian.orm.core.example.endity")
public class OrmCoreExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(OrmCoreExampleApplication.class, args);

        TestClass testClass = new TestClass();
        testClass.test();
    }
}

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
