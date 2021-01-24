package top.darian.orm.core.common.utils;

import java.util.Locale;


/***
 *
 * <p> copy from ibatis </p>
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  下午1:54
 */
public final class PropertyNamer {


    private PropertyNamer() {
        // Prevent Instantiation of Static Class
    }

    //methodToProperty方法会将方法名转换成属性名
    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException(
                    "Error parsing property name '"
                            + name
                            + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    //isProperty方法负责检测方法名是否对应属性名
    public static boolean isProperty(String name) {
        return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
    }

    public static boolean isGetter(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    public static boolean isSetter(String name) {
        return name.startsWith("set");
    }

}

