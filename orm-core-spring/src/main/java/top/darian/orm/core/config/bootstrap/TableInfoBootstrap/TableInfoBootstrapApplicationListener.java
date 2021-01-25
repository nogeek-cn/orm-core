package top.darian.orm.core.config.bootstrap.TableInfoBootstrap;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午9:03
 */

import com.alibaba.spring.context.OnceApplicationContextEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import top.darian.orm.core.bootstrap.TableInfoBootstrap;
import top.darian.orm.core.common.constants.CommonConstants;
import top.darian.orm.core.spring.TableInfoServiceBean;
import top.darian.orm.core.spring.beans.util.TableInfoBeanUtils;

import java.util.Map;

/**
 * The {@link ApplicationListener} for {@link TableInfoBootstrap}'s lifecycle when the {@link ContextRefreshedEvent}
 * and {@link ContextClosedEvent} raised
 *
 * @since 2.7.5
 */
public class TableInfoBootstrapApplicationListener
        extends OnceApplicationContextEventListener implements Ordered {

    private Logger logger = LoggerFactory.getLogger(TableInfoBootstrapApplicationListener.class);

    /**
     * The bean name of {@link TableInfoBootstrapApplicationListener}
     *
     * @since 2.7.6
     */
    public static final String BEAN_NAME = "tableInfoBootstrapApplicationListener";

    private final TableInfoBootstrap tableInfoBootstrap;

    private final boolean earlyInitialization;

    private final ApplicationContext applicationContext;

    public TableInfoBootstrapApplicationListener(ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.tableInfoBootstrap = TableInfoBootstrap.getInstance();
        TableInfoBeanUtils.setApplicationContext(applicationContext);

        String property = applicationContext.getEnvironment()
                .getProperty(CommonConstants.TABLE_INFO_BEAN_EARLY_INITIALIZATION);

        if (Boolean.TRUE.equals(Boolean.valueOf(property))) {
            earlyInitialization = true;
        } else {
            earlyInitialization = false;
        }
        logger.info("tableInfoServiceBean environment earlyInitialization: " + earlyInitialization);
    }

    @Override
    public void onApplicationContextEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshedEvent((ContextRefreshedEvent) event);
        } else if (event instanceof ContextClosedEvent) {
            onContextClosedEvent((ContextClosedEvent) event);
        }
    }

    private void onContextRefreshedEvent(ContextRefreshedEvent event) {
        if (earlyInitialization) {
            logger.info("tableInfoServiceBean.earlyInitialization is true");
            Map<String, TableInfoServiceBean> beansOfType =
                    applicationContext.getBeansOfType(TableInfoServiceBean.class);
            logger.info("tableInfoServiceBean.earlyInitialization bean count: " + beansOfType.size());
        }
        tableInfoBootstrap.start();
    }

    private void onContextClosedEvent(ContextClosedEvent event) {
        tableInfoBootstrap.stop();
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
