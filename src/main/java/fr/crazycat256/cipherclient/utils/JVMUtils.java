/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import fr.crazycat256.cipherclient.CipherClient;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class JVMUtils {

    /**
     * Check if a class exists
     * @param className The name of the class to check for
     * @return Whether the class exists
     */
    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Try to get a class by name
     * @param name The name of the class
     * @return The class if it exists, otherwise null
     */
    public static Class<?> tryGetClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Get a resource as an InputStream
     * @param path the path to the resource
     * @return The InputStream of the resource
     */
    public static InputStream getResourceAsStream(String path) {
        ClassLoader cl = CipherClient.isInjected ? ClassLoader.getSystemClassLoader() : Minecraft.class.getClassLoader();
        return cl.getResourceAsStream(path);
    }

    /**
     * Get all superclasses and interfaces of a class
     * @param klass The class to get the superclasses and interfaces of
     * @return A set containing all classes and interfaces
     */
    public static Set<Class<?>> getAllSuper(Class<?> klass) {
        Set<Class<?>> classes = new HashSet<>();

        Class<?> superclass = klass.getSuperclass();
        if (superclass != null) {
            classes.add(superclass);
            classes.addAll(getAllSuper(superclass));
        }

        for (Class<?> interfaceClass : klass.getInterfaces()) {
            classes.add(interfaceClass);
            classes.addAll(getAllSuper(interfaceClass));
        }

        return classes;
    }
}
