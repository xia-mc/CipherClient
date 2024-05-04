/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform;

import fr.crazycat256.cipherclient.utils.ASMUtils;
import fr.crazycat256.cipherclient.utils.Utils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Base class for all transformers <br>
 * This class is responsible for generating new bytecode from the class and its ClassNode
 */
public abstract class Transformer implements Opcodes {

    protected final String selfPath;
    private final Class<?> klass;
    private Exception exception;

    private static final boolean MCP = Utils.getMinecraftEnvironment() == Utils.McEnv.MCP;

    public Transformer(Class<?> klass) {
        this.klass = klass;
        this.selfPath = ASMUtils.getPath(this.getClass());
    }

    public Class<?> getKlass() {
        return klass;
    }

    public byte[] getNewClassBytes(ClassNode cn) {

        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Transform.class)) {
                continue;
            }

            method.setAccessible(true);
            Transform transform = method.getAnnotation(Transform.class);

            if (!Arrays.equals(method.getParameterTypes(), new Class<?>[]{MethodNode.class})) {
                throw new IllegalStateException("Method '" + method.getName() + "' has incorrect arguments");
            }

            MethodNode methodNode = null;
            for (MethodNode mn : cn.methods) {
                if (areEquals(transform, mn)) {
                    methodNode = mn;
                    break;
                }
            }
            if (methodNode == null) {
                throw new IllegalStateException("Method '" + transform.methodName() + "' not found in class '" + klass.getName() + "'");
            }


            try {
                method.invoke(this, methodNode);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                throw new RuntimeException("Failed to invoke method '" + method.getName() + "'", e);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private boolean areEquals(Transform transform, MethodNode mn) {
        String name = MCP || transform.obfuscatedName().isEmpty() ? transform.methodName() : transform.obfuscatedName();
        if (!mn.name.equals(name)) {
            return false;
        }

        StringBuilder transformDescBuild = new StringBuilder("(");
        for (Class<?> arg : transform.args()) {
            transformDescBuild.append(Type.getDescriptor(arg));
        }
        transformDescBuild.append(");");
        String transformDesc = transformDescBuild.toString();
        String mnDesc = mn.desc;
        int index = mnDesc.lastIndexOf(')');
        if (index != -1) {
            mnDesc = mnDesc.substring(0, index + 1) + ";";
        }

        return mnDesc.equals(transformDesc);
    }

    public void throwException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }
}
