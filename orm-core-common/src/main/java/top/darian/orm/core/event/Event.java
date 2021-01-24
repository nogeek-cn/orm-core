package top.darian.orm.core.event;


import java.util.EventObject;

/***
 *
 * An event object of orm-core is based on the Java standard {@link EventObject event}
 *
 * <p>Copy from Dubbo</p>
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  上午6:24
 * @since 1.0.0
 */
public abstract class Event extends EventObject {

    private static final long serialVersionUID = -1704315605423947137L;

    /**
     * The timestamp of event occurs
     */
    private final long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public Event(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}