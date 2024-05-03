/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the ClassFileTransformer interface to transform classes <br>
 * This is needed to get the actual bytecode of the class
 */
public class ClassTransformer implements ClassFileTransformer {

    private final Map<Class<?>, Transformer> transformers;

    public ClassTransformer(List<Transformer> transformers) {
        Map<Class<?>, Transformer> map = new HashMap<>();
        for (Transformer transformer : transformers) {
            map.put(transformer.getKlass(), transformer);
        }
        this.transformers = map;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        Transformer transformer = transformers.get(classBeingRedefined);

        if (transformer == null) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.EXPAND_FRAMES);

        try {
            return transformer.getNewClassBytes(cn);
        } catch (Exception e) {
            // Transformers cannot throw exceptions, this is a workaround to do so
            transformer.throwException(e);
            System.err.println("Error in transformer " + transformer.getKlass().getName());
            e.printStackTrace();
            throw new RuntimeException(e); // This does absolutely nothing
        }
    }
}
