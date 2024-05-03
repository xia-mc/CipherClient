/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

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

    /**
     * Get the last return instruction in a method
     * @param mn The method
     * @return The last return instruction
     */
    public static AbstractInsnNode getLastReturn(MethodNode mn) {
        AbstractInsnNode last = mn.instructions.getLast();
        while (last.getOpcode() != Opcodes.RETURN) {
            last = last.getPrevious();
        }
        return last;
    }
}
