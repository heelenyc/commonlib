package heelenyc.commonlib;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yicheng
 * @since 2016年4月27日
 * 
 */
public class ClassUtils {

    private static Set<Class<?>> primitivTypes = new HashSet<Class<?>>();
    
    static{
        primitivTypes.add(Byte.class);
        primitivTypes.add(Short.class);
        primitivTypes.add(Integer.class);
        primitivTypes.add(Long.class);
        primitivTypes.add(Float.class);
        primitivTypes.add(Double.class);
        primitivTypes.add(Character.class);
        primitivTypes.add(String.class);
        primitivTypes.add(Boolean.class);
        primitivTypes.add(Void.class);
    }
    /**
     * @param class1
     */
    public static boolean isCommonPrimitive(Class<?> clz) {
        if (clz.isPrimitive()) {
            return true;
        }

        if (primitivTypes.contains(clz)) {
            return true;
        }

        return false;
    }
}
