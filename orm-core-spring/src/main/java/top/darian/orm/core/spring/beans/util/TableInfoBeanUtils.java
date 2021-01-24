package top.darian.orm.core.spring.beans.util;

import org.springframework.context.ApplicationContext;
import top.darian.orm.core.config.spring.beans.factory.annotation.TableInfoServiceBeanNameBuilder;
import top.darian.orm.core.spring.TableInfoServiceBean;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  下午1:21
 */
public class TableInfoBeanUtils {

    private static ApplicationContext applicationContext;


    /**
     * 取出来 TableInfoServiceBean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TableInfoServiceBean<T> getBean(Class<T> clazz) {
        TableInfoServiceBean<T> tableInfoServiceBeanT = applicationContext.getBean(
                TableInfoServiceBeanNameBuilder.create(clazz).build(),
                TableInfoServiceBean.class);
        return tableInfoServiceBeanT;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        TableInfoBeanUtils.applicationContext = applicationContext;
    }
}
