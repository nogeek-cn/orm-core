package top.darian.orm.core.common.function;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午7:13
 */

import java.util.function.Function;

/**
 * {@link Function} with {@link Throwable}
 *
 * @param <T> the source type
 * @param <R> the return type
 * @see Function
 * @see Throwable
 * @since 2.7.5
 */
@FunctionalInterface
public interface ThrowableFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Throwable if met with any error
     */
    R apply(T t) throws Throwable;

    /**
     * Executes {@link ThrowableFunction}
     *
     * @param t the function argument
     * @return the function result
     * @throws RuntimeException wrappers {@link Throwable}
     */
    default R execute(T t) throws RuntimeException {
        R result = null;
        try {
            result = apply(t);
        } catch (Throwable e) {
            throw new RuntimeException(e.getCause());
        }
        return result;
    }

    /**
     * Executes {@link ThrowableFunction}
     *
     * @param t        the function argument
     * @param function {@link ThrowableFunction}
     * @param <T>      the source type
     * @param <R>      the return type
     * @return the result after execution
     */
    static <T, R> R execute(T t, ThrowableFunction<T, R> function) {
        return function.execute(t);
    }
}

