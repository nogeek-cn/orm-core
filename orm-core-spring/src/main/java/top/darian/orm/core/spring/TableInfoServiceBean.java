package top.darian.orm.core.spring;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import top.darian.orm.core.config.ServiceConfigBase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ServiceFactoryBean
 *
 * <p>copy from dubbo</p>
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @export
 * @date 2021/1/23  下午4:08
 */
public class TableInfoServiceBean<T> extends ServiceConfigBase<T> implements InitializingBean, DisposableBean,
        ApplicationContextAware, BeanNameAware, ApplicationEventPublisherAware {

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private ApplicationEventPublisher applicationEventPublisher;

    private String tableName;

    private Map<String, String> fieldColumnMap = new HashMap<String, String>();

    private ConcurrentMap<String, String> functionFieldMap = new ConcurrentHashMap<String, String>();

    public TableInfoServiceBean() {
        super();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }


    @Override
    public void destroy() throws Exception {
        // no need to call unexport() here, see
    }


    /**
     * @param applicationEventPublisher
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setFieldColumnMap(Map<String, String> fieldColumnMap) {
        this.fieldColumnMap = Collections.unmodifiableMap(fieldColumnMap);
    }

    public ConcurrentMap<String, String> getFunctionFieldMap() {
        return functionFieldMap;
    }


    public void setFunctionFieldMap(ConcurrentHashMap<String, String> functionFieldMap) {
        this.functionFieldMap = functionFieldMap;
    }

    public Map<String, String> getFieldColumnMap() {
        return fieldColumnMap;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
