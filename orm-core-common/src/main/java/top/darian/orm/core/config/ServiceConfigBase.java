package top.darian.orm.core.config;

/***
 * ServiceConfig
 *
 * @export
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午8:20
 */
public abstract class ServiceConfigBase<T> {

    /**
     * The interface name of the exported service
     */
    protected String tableClassName;

    /**
     * The interface class of the exported service
     */
    protected Class<?> tableClassClass;

    /**
     * The reference of the interface implementation
     */
    protected T ref;

    protected Class getServiceClass(T ref) {
        return ref.getClass();
    }


    public Class<?> getTableClassClass() {
        if (tableClassClass != null) {
            return tableClassClass;
        }
        try {
            if (tableClassName != null && tableClassName.length() > 0) {
                this.tableClassClass = Class.forName(tableClassName, true, Thread.currentThread()
                        .getContextClassLoader());
            }
        } catch (ClassNotFoundException t) {
            throw new IllegalStateException(t.getMessage(), t);
        }
        return tableClassClass;
    }

    public String getTableClassName() {
        return tableClassName;
    }

    public void setTableClassClass(Class<?> tableClassClass) {
        if (tableClassClass != null && !tableClassClass.isInterface()) {
            throw new IllegalStateException("The tableClassName class " + tableClassName + " is not a interface!");
        }
        this.tableClassClass = tableClassClass;
        setTableClassName(tableClassName == null ? null : tableClassClass.getName());
    }

    public void setTableClassName(String tableClassName) {
        this.tableClassName = tableClassName;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }
}