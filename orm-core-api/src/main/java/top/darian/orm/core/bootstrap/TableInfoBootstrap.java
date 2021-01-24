package top.darian.orm.core.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.darian.orm.core.event.GenericEventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午6:19
 */
public class TableInfoBootstrap extends GenericEventListener {

    private Logger logger = LoggerFactory.getLogger(TableInfoBootstrap.class);

    private static final String NAME = TableInfoBootstrap.class.getSimpleName();

    private AtomicBoolean started = new AtomicBoolean(false);

    private static volatile TableInfoBootstrap instance;

    public static TableInfoBootstrap getInstance() {
        if (instance == null) {
            synchronized (TableInfoBootstrap.class) {
                if (instance == null) {
                    instance = new TableInfoBootstrap();
                }
            }
        }
        return instance;
    }

    /**
     * Start the TableInfoBootstrap
     */
    public TableInfoBootstrap start() {
        if (started.compareAndSet(false, true)) {
            initialize();
            if (logger.isInfoEnabled()) {
                logger.info(NAME + " is starting...");
            }
            // 1. lazy bean


            if (logger.isInfoEnabled()) {
                logger.info(NAME + " has started.");
            }
        }
        return this;
    }


    /**
     * Initialize
     */
    public void initialize() {
        if (logger.isInfoEnabled()) {
            logger.info(NAME + " has been initialized!");
        }
    }

    public void stop() {

    }
}
