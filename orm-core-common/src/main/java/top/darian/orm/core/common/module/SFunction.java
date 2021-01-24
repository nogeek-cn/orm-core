package top.darian.orm.core.common.module;

import java.io.Serializable;
import java.util.function.Function;

/***
 *使Function获取序列化能力
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  下午2:13
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}