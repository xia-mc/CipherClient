/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform;

import fr.crazycat256.cipherclient.transform.transformers.*;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all transformers <br>
 * This class is responsible for initializing all transformers and applying them to the classes
 * It needs an Instrumentation instance to retransform the classes at runtime, so it implies loading an agent at some point
 */
public class TransformManager {

    private static final CopyOnWriteArrayList<Transformer> transformers = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<Transformer> getAll() {
        return new CopyOnWriteArrayList<>(transformers);
    }


    public static void init() {

        add(new EventBusTransformer());
        add(new EntityRendererTransformer());
        add(new EntityClientPlayerMPTransformer());
        add(new BlockLiquidTransformer());

    }

    public static void applyTransformers(Instrumentation inst) {
        Objects.requireNonNull(inst, "Instrumentation instance cannot be null");

        ClassTransformer classTransformer = new ClassTransformer(transformers);
        inst.addTransformer(classTransformer, true);

        for (Transformer transformer : transformers) {
            try {
                inst.retransformClasses(transformer.getKlass());
            } catch (UnmodifiableClassException e) {
                throw new RuntimeException(e);
            }
        }

        for (Transformer transformer : transformers) {
            if (transformer.getException() != null) {
                throw new RuntimeException("Error in transformer " + transformer.getClass().getSimpleName()
                    + " failed to transform " + transformer.getKlass().getName(), transformer.getException());
            }
        }
    }

    private static void add(Transformer transformer) {
        transformers.add(transformer);
    }

}
