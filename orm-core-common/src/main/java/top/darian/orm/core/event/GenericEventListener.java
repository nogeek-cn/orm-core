package top.darian.orm.core.event;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午6:30
 */

import top.darian.orm.core.common.function.ThrowableConsumer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Stream.of;
import static top.darian.orm.core.common.function.ThrowableFunction.execute;

/**
 * @see Event
 * @see EventListener
 * @since 2.7.5
 */
public abstract class GenericEventListener implements EventListener<Event> {

    private final Method onEventMethod;

    private final Map<Class<?>, Set<Method>> handleEventMethods;

    protected GenericEventListener() {
        this.onEventMethod = findOnEventMethod();
        this.handleEventMethods = findHandleEventMethods();
    }

    private Method findOnEventMethod() {
        return execute(getClass(), listenerClass -> listenerClass.getMethod("onEvent", Event.class));
    }

    private Map<Class<?>, Set<Method>> findHandleEventMethods() {
        // Event class for key, the eventMethods' Set as value
        Map<Class<?>, Set<Method>> eventMethods = new HashMap<>();
        of(getClass().getMethods())
                .filter(this::isHandleEventMethod)
                .forEach(method -> {
                    Class<?> paramType = method.getParameterTypes()[0];
                    Set<Method> methods = eventMethods.computeIfAbsent(paramType, key -> new LinkedHashSet<>());
                    methods.add(method);
                });
        return eventMethods;
    }

    @Override
    public final void onEvent(Event event) {
        Class<?> eventClass = event.getClass();
        handleEventMethods.getOrDefault(eventClass, emptySet()).forEach(method -> {
            ThrowableConsumer.execute(method, m -> {
                m.invoke(this, event);
            });
        });
    }

    /**
     * The {@link Event event} handle methods must meet following conditions:
     * <ul>
     * <li>not {@link #onEvent(Event)} method</li>
     * <li><code>public</code> accessibility</li>
     * <li><code>void</code> return type</li>
     * <li>no {@link Exception exception} declaration</li>
     * <li>only one {@link Event} type argument</li>
     * </ul>
     *
     * @param method
     * @return
     */
    private boolean isHandleEventMethod(Method method) {

        if (onEventMethod.equals(method)) { // not {@link #onEvent(Event)} method
            return false;
        }

        if (!Modifier.isPublic(method.getModifiers())) { // not public
            return false;
        }

        if (!void.class.equals(method.getReturnType())) { // void return type
            return false;
        }

        Class[] exceptionTypes = method.getExceptionTypes();

        if (exceptionTypes.length > 0) { // no exception declaration
            return false;
        }

        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) { // not only one argument
            return false;
        }

        if (!Event.class.isAssignableFrom(paramTypes[0])) { // not Event type argument
            return false;
        }

        return true;
    }
}
