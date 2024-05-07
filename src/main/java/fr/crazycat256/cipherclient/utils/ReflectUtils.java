/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.settings.KeyBinding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {

    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final Map<String, Method> methodCache = new HashMap<>();


    public static Field getField(Class<?> klass, String fieldName) {
        String key = klass.getName() + "." + fieldName;
        if (fieldCache.containsKey(key)) {
            return fieldCache.get(key);
        }
        fieldName = Mappings.getFieldName(klass, fieldName);
        Field field = ReflectionHelper.findField(klass, fieldName);
        fieldCache.put(key, field);
        return field;
    }

    public static <E> Method getMethod(Class<? super E> klass, String methodName, Class<?>... parameterTypes) {
        if (parameterTypes == null) {
            parameterTypes = new Class[0];
        }
        methodName = Mappings.getMethodName(klass, methodName, parameterTypes);
        String key = klass.getName() + "." + Mappings.getMethodSig(methodName, parameterTypes);
        if (methodCache.containsKey(key)) {
            return methodCache.get(key);
        }
        Method method = ReflectionHelper.findMethod(klass, null, new String[]{methodName}, parameterTypes);
        methodCache.put(key, method);
        return method;
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T get(Class <? super E> classToAccess, E instance, String fieldName) {
        Field field = getField(classToAccess, fieldName);
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, E> void set(Class <? super E> classToAccess, E instance, String fieldName, T value) {
        Field field = getField(classToAccess, fieldName);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object call(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setPressed(KeyBinding keyBinding, boolean pressed) {
        set(KeyBinding.class, keyBinding, "pressed", pressed);
        if (!pressed) {
            set(KeyBinding.class, keyBinding, "pressTime", 0);
        }
    }

}
