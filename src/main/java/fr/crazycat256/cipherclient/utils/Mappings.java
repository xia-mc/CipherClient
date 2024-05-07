/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mappings {

    private static final boolean MCP = Utils.getMinecraftEnvironment() == Utils.McEnv.MCP;

    private static final Map<String, Map<String, String>> fields = new HashMap<>();
    private static final Map<String, Map<String, String>> methods = new HashMap<>();

    static {
        loadMappings("fields.json", fields);
        loadMappings("methods.json", methods);
    }

    /**
     * Get the obfuscated name of a field
     * @param klass The class the field is in
     * @param name The mapped name of the field
     * @return The obfuscated name of the field or the original name if no mapping is found
     */
    public static String getFieldName(Class<?> klass, String name) {
        if (MCP || !fields.containsKey(klass.getName())) {
            return name;
        }
        //String fieldName = fields.get(klass.getName()).getOrDefault(name, name);
        Set<Class<?>> classes = JVMUtils.getAllSuper(klass);
        classes.add(klass);
        String fieldName = null;
        for (Class<?> clazz : classes) {
            if (fields.containsKey(clazz.getName())) {
                fieldName = fields.get(clazz.getName()).get(name);
                if (fieldName != null) {
                    break;
                }
            }
        }
        if (fieldName == null) {
            return name;
        }
        try {
            klass.getDeclaredField(fieldName);
            return fieldName;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field " + fieldName + " (" + name + ") not found in class " + klass.getName());
        }
    }

    /**
     * Get the obfuscated name of a method
     * @param klass The class the method is in
     * @param name The mapped name of the method
     * @return The obfuscated name of the method or the original name if no mapping is found
     */
    public static String getMethodName(Class<?> klass, String name, Class<?>... parameterTypes) {
        if (MCP) {
            return name;
        }
        String methodSig = getMethodSig(name, parameterTypes);
        Set<Class<?>> classes = JVMUtils.getAllSuper(klass);
        classes.add(klass);
        String methodName = null;
        for (Class<?> clazz : classes) {
            if (methods.containsKey(clazz.getName())) {
                if (methods.get(clazz.getName()).containsKey(methodSig)) {
                    methodName = methods.get(clazz.getName()).get(methodSig);
                    break;
                }
            }
        }
        if (methodName == null) {
            return name;
        }
        try {
            klass.getDeclaredMethod(methodName, parameterTypes);
            return methodName;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method " + methodName + " (" + name + ") not found in class " + klass.getName());
        }
    }

    /**
     * Return a signature for a method based on its name and parameter types <br>
     * This does not need to be parsed as it is only used for comparison
     */
    public static String getMethodSig(String methodName, Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName).append('(');
        for (Class<?> parameterType : parameterTypes) {
            sb.append(parameterType.getName()).append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.append(')').toString();
    }

    private static void loadMappings(String path, Map<String, Map<String, String>> map) {
        try (InputStream is = JVMUtils.getResourceAsStream(path)) {

            InputStreamReader reader = new InputStreamReader(is);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
            Map<String, Map<String, String>> tempMap = gson.fromJson(reader, type);

            map.putAll(tempMap);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load mappings", e);
        }
    }
}
