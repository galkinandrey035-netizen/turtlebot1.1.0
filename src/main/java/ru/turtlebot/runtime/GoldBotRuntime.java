package ru.turtlebot.runtime;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Runtime bridge to the bundled GoldBot implementation.
 * This class must stay outside any package declared by a mixin config.
 */
public final class GoldBotRuntime {
    private GoldBotRuntime() {}

    public static boolean skyActive(Object world) {
        return (boolean) invokeStatic("dev.goldbot.SkyFx", "active", world);
    }

    public static int fogRgb(Object camera, Object world) {
        return (int) invokeStatic("dev.goldbot.SkyFx", "fogRgb", camera, world);
    }

    public static boolean fogBlocked(Object camera) {
        return (boolean) invokeStatic("dev.goldbot.SkyFx", "fogBlocked", camera);
    }

    public static void viewModelApply(Object matrices) {
        invokeStatic("dev.goldbot.ViewModel", "apply", matrices);
    }

    public static void itemPhysicsTranslate(Object matrices, float x, float y, float z) {
        invokeStatic("dev.goldbot.ItemPhysics", "translate", matrices, x, y, z);
    }

    public static void itemPhysicsRotate(Object matrices, Object rotation) {
        invokeStatic("dev.goldbot.ItemPhysics", "rotate", matrices, rotation);
    }

    public static void freeCamFrame(Object freeCam, Object client) {
        invokeInstance(freeCam, "frame", client);
    }

    private static Object invokeStatic(String className, String methodName, Object... args) {
        try {
            Class<?> owner = Class.forName(className);
            Method method = findMethod(owner, methodName, args);
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new IllegalStateException(methodName + " is not static");
            }
            return method.invoke(null, args);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("GoldBot call failed: " + className + "." + methodName, exception);
        }
    }

    private static Object invokeInstance(Object instance, String methodName, Object... args) {
        if (instance == null) return null;
        try {
            Method method = findMethod(instance.getClass(), methodName, args);
            return method.invoke(instance, args);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("GoldBot call failed: " + methodName, exception);
        }
    }

    private static Method findMethod(Class<?> owner, String methodName, Object[] args)
            throws NoSuchMethodException {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int i = 0; i < parameters.length; i++) {
                if (args[i] == null) continue;
                Class<?> expected = wrap(parameters[i]);
                if (!expected.isAssignableFrom(args[i].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) return method;
        }
        for (Method method : owner.getDeclaredMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int i = 0; i < parameters.length; i++) {
                if (args[i] == null) continue;
                if (!wrap(parameters[i]).isAssignableFrom(args[i].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(owner.getName() + "." + methodName + "/" + args.length);
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == int.class) return Integer.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == long.class) return Long.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        return type;
    }
}