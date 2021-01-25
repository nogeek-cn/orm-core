package top.darian.orm.core.example.understand;

import top.darian.orm.core.BeanToDataBaseUtils;
import top.darian.orm.core.example.endity.ClassDO;
import top.darian.orm.core.example.endity.SchoolDO;
import top.darian.orm.core.example.endity.UserDO;
import top.darian.orm.core.spring.TableInfoServiceBean;
import top.darian.orm.core.spring.beans.util.TableInfoBeanUtils;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  下午4:58
 */
public class UnderStand {

    public void test() {
        testOne(SchoolDO.class);
        testOne(UserDO.class);
        testOne(ClassDO.class);

        testField();
        testField();
        testFunction();
        testFunction();
        testFunction();

        System.out.println(TableInfoBeanUtils.getBean(UserDO.class).getFunctionFieldMap());
        System.out.println("UserDO.functionColumnMap().size():\t"
                + TableInfoBeanUtils.getBean(UserDO.class).getFunctionFieldMap().size());
    }


    private void testOne(Class<?> clazz) {
        TableInfoServiceBean<?> bean = TableInfoBeanUtils.getBean(clazz);


        System.out.println();
        System.out.println(String.format("tableInfoService.ref[%s]:beanToString\t[%s]", clazz.getSimpleName(), bean));
        System.out.println(String.format("tableInfoService.ref[%s].interfaceName:\t[%s]", clazz.getSimpleName(), bean.getTableClassName()));
        System.out.println(String.format("tableInfoService.ref[%s].interfaceClass:\t[%s]", clazz.getSimpleName(), bean.getTableClassClass()));
        System.out.println(String.format("tableInfoService.ref[%s].fieldColumnMap:\t[%s]", clazz.getSimpleName(), bean.getFieldColumnMap()));
        System.out.println(String.format("tableInfoService.ref[%s].functionFieldMap:\t[%s]", clazz.getSimpleName(), bean.getFunctionFieldMap()));
        System.out.println(String.format("tableInfoService.ref[%s].tableName:\t[%s]", clazz.getSimpleName(), bean.getTableName()));


    }


    public void testFunction() {

        System.out.println("UserDO::getUserName\t=\t" +
                BeanToDataBaseUtils.getColumnByFunctionName(UserDO::getUserName, UserDO.class));

        System.out.println("UserDO::getName\t=\t"
                + BeanToDataBaseUtils.getColumnByFunctionName(UserDO::getName, UserDO.class));
    }

    public void testField() {
        System.out.println("UserDO.name\t=\t" +
                BeanToDataBaseUtils.getColumnByFieldName("name", UserDO.class));

        System.out.println("UserDO.userName\t=\t" +
                BeanToDataBaseUtils.getColumnByFieldName("userName", UserDO.class));
    }
}
