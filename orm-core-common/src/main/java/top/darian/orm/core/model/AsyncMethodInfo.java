package top.darian.orm.core.model;


import java.lang.reflect.Method;


/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午3:44
 */
public class AsyncMethodInfo {
    // callback instance when async-call is invoked
    private Object oninvokeInstance;

    // callback method when async-call is invoked
    private Method oninvokeMethod;

    // callback instance when async-call is returned
    private Object onreturnInstance;

    // callback method when async-call is returned
    private Method onreturnMethod;

    // callback instance when async-call has exception thrown
    private Object onthrowInstance;

    // callback method when async-call has exception thrown
    private Method onthrowMethod;

    public Object getOninvokeInstance() {
        return oninvokeInstance;
    }

    public void setOninvokeInstance(Object oninvokeInstance) {
        this.oninvokeInstance = oninvokeInstance;
    }

    public Method getOninvokeMethod() {
        return oninvokeMethod;
    }

    public void setOninvokeMethod(Method oninvokeMethod) {
        this.oninvokeMethod = oninvokeMethod;
    }

    public Object getOnreturnInstance() {
        return onreturnInstance;
    }

    public void setOnreturnInstance(Object onreturnInstance) {
        this.onreturnInstance = onreturnInstance;
    }

    public Method getOnreturnMethod() {
        return onreturnMethod;
    }

    public void setOnreturnMethod(Method onreturnMethod) {
        this.onreturnMethod = onreturnMethod;
    }

    public Object getOnthrowInstance() {
        return onthrowInstance;
    }

    public void setOnthrowInstance(Object onthrowInstance) {
        this.onthrowInstance = onthrowInstance;
    }

    public Method getOnthrowMethod() {
        return onthrowMethod;
    }

    public void setOnthrowMethod(Method onthrowMethod) {
        this.onthrowMethod = onthrowMethod;
    }
}
