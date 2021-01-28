package top.darian.orm.core.common.module;

import java.io.Serializable;
import java.util.function.BiConsumer;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/28  下午9:18
 */
@FunctionalInterface
public interface SBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {
}
