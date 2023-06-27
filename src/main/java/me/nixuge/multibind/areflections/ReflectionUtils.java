package me.nixuge.multibind.areflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

public class ReflectionUtils {
    static final Map<Class<?>, Map<Class<?>, Field>> FIELD_CACHE = Maps.newHashMap();
    // TODO: use this
    static final Map<Class<?>, Map<Class<?>, Field>> METHOD_CACHE = Maps.newHashMap();

    /**
     * Uses Java's reflection API to find an inaccessible field of the given
     * type in the given class.
     * <p>
     * This method's result is undefined if the given class has multiple
     * fields of the same type.
     *
     * @param typeOfClass
     *            Class that the field should be read from
     * @param typeOfField
     *            The type of the field
     * @return The field, with {@link Field#setAccessible(boolean)} already called
     */
    public static Field findField(Class<?> typeOfClass, Class<?> typeOfField) {
        if (FIELD_CACHE.containsKey(typeOfClass)) {
            Map<Class<?>, Field> fields = FIELD_CACHE.get(typeOfClass);
            if (fields.containsKey(typeOfField)) {
                return fields.get(typeOfField);
            }
        }

        Field[] fields = typeOfClass.getDeclaredFields();

        for (Field f : fields) {
            if (f.getType().equals(typeOfField)) {
                try {
                    f.setAccessible(true);

                    if (!FIELD_CACHE.containsKey(typeOfClass)) {
                        FIELD_CACHE.put(typeOfClass, Maps.<Class<?>, Field>newHashMap());
                    }

                    FIELD_CACHE.get(typeOfClass).put(typeOfField, f);

                    return f;
                } catch (Exception e) {
                    throw new RuntimeException(
                            "NoChunkUnload: Couldn't get private Field of type \""
                                    + typeOfField + "\" from class \"" + typeOfClass
                                    + "\" !", e);
                }
            }
        }

        throw new RuntimeException(
                "NoChunkUnload: Couldn't find any Field of type \""
                        + typeOfField + "\" from class \"" + typeOfClass
                        + "\" !");
    }

	public static Method getMethodFromNameArgs(Class<?> clazz, String methodName, Class<?>... args) {
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (!method.getName().equals(methodName))
                continue;

            Class<?>[] paramTypes = method.getParameterTypes();
            int argLength = paramTypes.length;

            if (argLength != args.length)
                continue;

            boolean isMatching = true;

            for (int i = 0; i < argLength; i++) {
                Class<?> currentArg = args[i];
                Class<?> currentParam = paramTypes[i];

                if (args[i] == null) // Just skip if null
                    continue;

                if (currentArg != currentParam) {
                    isMatching = false;
                    break;
                }
            }
            if (isMatching)
                return method;
        }

        return null;
    }

	public static Method getMethodFromNameAlone(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName))
                return method;
        }

        return null;
    }
}
