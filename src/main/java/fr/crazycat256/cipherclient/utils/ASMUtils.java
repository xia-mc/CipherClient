/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

public class ASMUtils {

    /**
     * Get the path of a class <br>
     * This is useful for referencing classes in ASM and prevent the code from breaking at the first refactoring
     * @param klass The class
     * @return The path, like "fr/crazycat256/cipherclient/utils/ASMUtils"
     */
    public static String getPath(Class<?> klass) {
        return klass.getName().replace('.', '/');
    }
}
